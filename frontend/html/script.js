const WIDTH = 160, HEIGHT = 144;
let sessionId = null;
let running = false;

const status_el = document.getElementById("status");
const canvas = document.getElementById("screen");
const ctx = canvas.getContext("2d");
const image_data = ctx.createImageData(WIDTH, HEIGHT);

async function new_session() {
    const res = await fetch(`/session/`, { method: "POST" });
    const data = await res.json();
    sessionId = data.session_id;
    status_el.textContent = `Session #${sessionId} running`;
    running = true;
    poll_framebuffer();
}

// poll framebuffer (anything but a websocket)
async function poll_framebuffer() {
    while (running) {
        try {
            const res = await fetch(`/session/${sessionId}/framebuffer`, {
                method: "GET",
                priority: "high",
            });

            if (!res.ok) {
                await sleep(200);
                continue;
            }

            const buf = await res.arrayBuffer();
            const bytes = new Uint8ClampedArray(buf);
            if (bytes.length === WIDTH * HEIGHT * 4) {
                image_data.data.set(bytes);
                ctx.putImageData(image_data, 0, 0);
            }
        } catch (e) {
            console.error("Framebuffer error:", e);
            await sleep(200);
        }

        // 60fps = 16.66667ms
        await sleep(16);
    }
}

// send input
async function send_input(inputState) {
    if (!sessionId) return;
    try {
        await fetch(`/session/${sessionId}/input`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(inputState),
        });
    } catch (e) { console.error("Input send failed:", e); }
}

// keybinds
const keyMap = {
    ArrowUp: "up",
    ArrowDown: "down",
    ArrowLeft: "left",
    ArrowRight: "right",
    z: "a",
    x: "b",
    Enter: "start",
    Shift: "select",
};

// input mask
const state = { up: false, down: false, left: false, right: false, a: false, b: false, start: false, select: false };

document.addEventListener("keydown", e => {
    const k = keyMap[e.key];

    if (k && !state[k]) {
        state[k] = true;
        send_input(state);
    }
});

document.addEventListener("keyup", e => {
    const k = keyMap[e.key];

    if (k && state[k]) {
        state[k] = false;
        send_input(state);
    }
});

function sleep(ms) {
    return new Promise(r => setTimeout(r, ms));
}

document.getElementById("new-session").onclick = new_session;

document.getElementById("load-rom").onclick = async () => {
    if (!sessionId) {
        alert("Start a session first!");
        return;
    }

    const fileInput = document.getElementById("rom-file");
    if (!fileInput.files.length) {
        alert("Please select a ROM file");
        return;
    }

    const file = fileInput.files[0];
    const arrayBuffer = await file.arrayBuffer();

    try {
        const res = await fetch(`/session/${sessionId}/load`, {
            method: "POST",
            headers: { "Content-Type": "application/octet-stream" },
            body: arrayBuffer,
        });

        const data = await res.json();
        if (res.ok) {
            status_el.textContent = `Session #${sessionId} running: ${file.name} loaded`;
        } else {
            alert("ROM load failed: " + data.error);
        }
    } catch (e) {
        console.error("ROM load error:", e);
        alert("ROM load failed, check console");
    }
};
