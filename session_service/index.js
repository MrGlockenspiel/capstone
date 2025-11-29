import express from "express";
import bodyParser from "body-parser";
import fetch from "node-fetch";
import { randomInt } from "crypto";

const app = express();

app.use(bodyParser.json());

const PPU_SERVICE = process.env.PPU_SERVICE || "http://ppu_service:8080";
const INPUT_SERVICE = process.env.INPUT_SERVICE || "http://input_service:8080";
const CART_SERVICE = process.env.CART_SERVICE || "http://cartridge_service:8080";
const CPU_SERVICE = process.env.CART_SERVICE || "http://cpu_service:8080";

// in memory session store maybe ill use redis later
const sessions = new Map();

/**
 * POST /session
 * Creates a new session and returns an integer session ID
 */
app.post("/", (req, res) => {
    const sessionId = randomInt(1_000_000, 9_999_999);
    const emulatorId = sessionId;

    sessions.set(sessionId, {
        emulatorId,
        createdAt: new Date(),
    });

    res.json({ session_id: sessionId, emulator_id: emulatorId });
});

/**
 * GET /session/:id/framebuffer
 * Proxies raw framebuffer bytes from PPU service
 */
app.get("/:id/framebuffer", async (req, res) => {
    const id = parseInt(req.params.id, 10);
    if (!sessions.has(id)) return res.status(404).json({ error: "session not found" });

    try {
        const resp = await fetch(`${PPU_SERVICE}/${id}/framebuffer`);
        if (!resp.ok) return res.status(resp.status).json({ error: "ppu service error" });

        res.setHeader("Content-Type", "application/octet-stream");
        res.setHeader("Cache-Control", "no-store");

        resp.body.pipe(res);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "failed to fetch framebuffer" });
    }
});

/**
 * POST /session/:id/input
 * Forwards input JSON to input service
 */
app.post("/:id/input", async (req, res) => {
    const id = parseInt(req.params.id, 10);
    if (!sessions.has(id)) {
        return res.status(404).json({ error: "session not found" });
    }

    try {
        const resp = await fetch(`${INPUT_SERVICE}/${id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(req.body),
        });
        if (!resp.ok) {
            const text = await resp.text();
            return res.status(resp.status).json({ error: "input service error", text });
        }
        res.status(204).end();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "failed to forward input" });
    }
});

/**
 * POST /session/:id/load
 * Loads a cartridge with the cartridge service
 */
app.post("/:id/load", async (req, res) => {
    const id = parseInt(req.params.id, 10);
    if (!sessions.has(id)) {
        return res.status(404).json({ error: "session not found" });
    }

    let buffers = [];
    req.on("data", chunk => buffers.push(chunk));
    req.on("end", async () => {
        const romBuffer = Buffer.concat(buffers);
        if (!romBuffer.length) {
            return res.status(400).json({ error: "No ROM uploaded" });
        }

        try {
            const cartridgeServiceUrl = `${CART_SERVICE}/${id}/load`;
            const fetchRes = await fetch(cartridgeServiceUrl, {
                method: "POST",
                headers: { "Content-Type": "application/octet-stream" },
                body: romBuffer,
            });

            if (!fetchRes.ok) {
                return res.status(500).json({ error: "Failed to load ROM" });
            }

            res.json({ success: true, message: "ROM loaded" });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: "Internal error" });
        }
    });

    req.on("error", err => {
        console.error(err);
        res.status(500).json({ error: "Upload failed" });
    });
});

/**
 * POST /session/:id/step
 * Forwards CPU step request
 */
app.post("/:id/step", async (req, res) => {
    const id = parseInt(req.params.id, 10);
    if (!sessions.has(id)) return res.status(404).json({ error: "session not found" });

    try {
        const resp = await fetch(`${CPU_SERVICE}/${id}/step`, { method: "POST" });
        if (!resp.ok) {
            const text = await resp.text();
            return res.status(resp.status).json({ error: "CPU step failed", text });
        }

        res.status(204).end();
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "CPU step error" });
    }
});

/**
 * GET /debug/:id
 * Proxies CPU debug info
 */
app.get("/:id/debug", async (req, res) => {
    const id = parseInt(req.params.id, 10);
    if (!sessions.has(id)) return res.status(404).json({ error: "session not found" });

    try {
        const resp = await fetch(`${CPU_SERVICE}/debug/${id}`);
        if (!resp.ok) {
            const text = await resp.text();
            return res.status(resp.status).json({ error: "CPU debug failed", text });
        }

        const data = await resp.json();
        res.json(data);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "CPU debug error" });
    }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => console.log(`Session service running on port ${PORT}`));
