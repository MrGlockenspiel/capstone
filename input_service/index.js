import express from "express";
import bodyParser from "body-parser"

const app = express();

app.use(bodyParser.json());

/**
 * POST /:id
 * Write to input registers of session
 */
app.post("/:id", async (req, res) => {
    try {
        const id = req.params.id;
        const state = req.body;

        // read 0xFF00
        const read_resp = await fetch(`http://memory_service:8080/${id}/65280`);
        const read_buf = Buffer.from(await read_resp.arrayBuffer());
        let ff00 = read_buf[0];

        // determine selection bits
        const select_buttons = (ff00 & 0x20) === 0;  // bit5 = 0
        const select_dpad = (ff00 & 0x10) === 0;  // bit4 = 0

        // make lower nibble
        let nibble;

        if (select_buttons) {
            nibble =
                ((state.start ? 0 : 1) << 3) |
                ((state.select ? 0 : 1) << 2) |
                ((state.b ? 0 : 1) << 1) |
                ((state.a ? 0 : 1) << 0);
        } else if (select_dpad) {
            nibble =
                ((state.down ? 0 : 1) << 3) |
                ((state.up ? 0 : 1) << 2) |
                ((state.left ? 0 : 1) << 1) |
                ((state.right ? 0 : 1) << 0);
        } else {
            nibble = 0xF; // if neither is set, hardware returns 0xF
        }

        // update 0xFF00
        ff00 = (ff00 & 0xF0) | nibble;

        // write 0xFF00
        await fetch(`http://memory_service:8080/${id}/65280`, {
            method: "POST",
            headers: { "Content-Type": "application/octet-stream" },
            body: Buffer.from([ff00])  // raw 1-byte buffer
        });

        res.json({ ok: true, ff00 });

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "failed to update FF00" });
    }
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => console.log(`Input service running on port ${PORT}`));
