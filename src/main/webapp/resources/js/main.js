const canvas = document.getElementById("graph");
const ctx = canvas.getContext("2d");
let points = [];

function getSelectedR() {
    const checked = document.querySelector('#r-chips input[name="r-chip"]:checked');
    return checked ? parseFloat(checked.value) : null;
}

const form = document.querySelector('form.form');
const xInput = document.getElementById('x');
const yInput = document.getElementById('y');
const rHidden = document.getElementById('r');

function normalizeNumber(str) {
    const s = str.replace(',', '.').trim();
    return s;
}

function isValidCoord(str) {
    const s = normalizeNumber(str);
    if (!/^[-+]?\d+(\.\d{1,3})?$/.test(s)) return false;
    const v = parseFloat(s);
    return Number.isFinite(v) && v >= -3 && v <= 3;
}

if (form) {
    form.addEventListener('submit', (e) => {
        const xStr = xInput.value;
        if (!isValidCoord(xStr)) {
            e.preventDefault();
            alert("X должен быть числом от -3 до 3, не более 3 знаков после точки");
            return;
        }

        const yStr = yInput.value;
        if (!isValidCoord(yStr)) {
            e.preventDefault();
            alert("Y должен быть числом от -3 до 3, не более 3 знаков после точки");
            return;
        }

        const R = getSelectedR();
        if (R === null) {
            e.preventDefault();
            alert("Выбери R");
            return;
        }

        if (rHidden) rHidden.value = R.toString();

        const xNorm = parseFloat(normalizeNumber(xStr));
        const yNorm = parseFloat(normalizeNumber(yStr));
        xInput.value = xNorm.toFixed(3);
        yInput.value = yNorm.toFixed(3);
    });
}

const resultsBody = document.getElementById('results-body');

function appendResultRow(json) {
    if (!resultsBody) return;
    const empty = resultsBody.querySelector('.empty-row');
    if (empty) empty.remove();

    const tr = document.createElement('tr');
    tr.className = json.hit ? 'hit' : 'miss';
    tr.innerHTML = `
    <td>${json.hit ? 'Попадание' : 'Промах'}</td>
    <td>${Number(json.x).toFixed(3)}</td>
    <td>${Number(json.y).toFixed(3)}</td>
    <td>${json.r}</td>
    <td>${(Number(json.execMs) || 0).toFixed(3)}</td>
    <td>${json.timestamp || ''}</td>
  `;
    resultsBody.appendChild(tr);
}

function drawGraph(R = 1) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.strokeStyle = "white";
    ctx.fillStyle = "white";
    ctx.font = "12px Arial";
    ctx.textBaseline = "middle";

    ctx.beginPath();
    ctx.moveTo(0, 300); ctx.lineTo(600, 300);
    ctx.moveTo(300, 0); ctx.lineTo(300, 600);
    ctx.stroke();

    ctx.beginPath(); ctx.moveTo(300, 0); ctx.lineTo(294, 15); ctx.lineTo(306, 15); ctx.closePath(); ctx.fill();
    ctx.beginPath(); ctx.moveTo(600, 300); ctx.lineTo(585, 306); ctx.lineTo(585, 294); ctx.closePath(); ctx.fill();

    function tick(x1,y1,x2,y2){ ctx.beginPath(); ctx.moveTo(x1,y1); ctx.lineTo(x2,y2); ctx.stroke(); }

    tick(500,305,500,295); tick(400,305,400,295); tick(100,305,100,295); tick(200,305,200,295);
    tick(295,100,305,100); tick(295,200,305,200); tick(295,400,305,400); tick(295,500,305,500);

    const scale = 200 / R;
    ctx.fillText("R/2", 300 + scale * R/2 - 10, 320);
    ctx.fillText("R",   400 + scale * R/2 - 5,  320);
    ctx.fillText("-R/2",100 + scale * R/2 - 15, 320);
    ctx.fillText("-R",  scale * R/2 - 10,       320);

    ctx.fillText("R/2", 310, 100 + scale * R/2 + 5);
    ctx.fillText("R",   310, scale * R/2);
    ctx.fillText("-R/2",310, 300 + scale * R/2);
    ctx.fillText("-R",  310, 400 + scale * R/2);

    ctx.fillText("X", 590, 320);
    ctx.fillText("Y", 310, 10);

    ctx.fillStyle = "rgba(0,255,84,0.14)";
    ctx.fillRect(500 - scale * R, 300, scale*R, scale*R);

    ctx.fillStyle = "rgba(0,60,255,0.15)";
    ctx.beginPath(); ctx.moveTo(300,300); ctx.lineTo(100+scale*R,200); ctx.lineTo(500,100+scale*R);
    ctx.closePath(); ctx.fill();

    ctx.fillStyle = "rgba(255,0,0,0.16)";
    ctx.beginPath(); ctx.moveTo(300,300);
    ctx.arc(300,300, scale*R/2, Math.PI, Math.PI*1.5, false);
    ctx.closePath(); ctx.fill();

    for (let p of points) {
        ctx.beginPath();
        ctx.fillStyle = p.hit ? "green" : "red";
        ctx.arc(300 + p.x * scale, 300 - p.y * scale, 3, 0, 2 * Math.PI);
        ctx.fill();
    }
}

function round3(v){ return Math.round(v*1000)/1000; }

function getClickCoordinates(evt, R) {
    const rect = canvas.getBoundingClientRect();
    const x = evt.clientX - rect.left;
    const y = evt.clientY - rect.top;
    const scale = 200 / R;
    const xVal = (x - 300) / scale;
    const yVal = (300 - y) / scale;
    return { xVal: round3(xVal), yVal: round3(yVal) };
}

async function checkHit(x, y, R) {
    const params = new URLSearchParams({
        x: x.toFixed(3),
        y: y.toFixed(3),
        r: R.toString()
    });

    // /webLab3/index.xhtml -> /webLab3/api/point
    const parts = window.location.pathname.split("/");
    const context = parts.length > 1 ? "/" + parts[1] : "";
    const url = context + "/api/point";

    const resp = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: params.toString()
    });

    if (!resp.ok) {
        const text = await resp.text();
        console.error("Bad request:", resp.status, text);
        throw new Error("Bad request");
    }

    const text = await resp.text();
    try {
        return JSON.parse(text);
    } catch (e) {
        console.error("JSON parse error. Raw response:", text);
        throw e;
    }
}


canvas.addEventListener("click", async (evt) => {
    try {
        const R = getSelectedR();
        if (!R) {
            alert("Выбери R");
            return;
        }

        const {xVal, yVal} = getClickCoordinates(evt, R);
        const json = await checkHit(xVal, yVal, R);

        points.push({x: xVal, y: yVal, hit: json.hit});
        drawGraph(R);
        appendResultRow(json);
    } catch (e){
        console.error("AJAX/error", e);
        alert("Не удалось проверить точку. Смотрите детали в консоли.");
    }
});

document.querySelectorAll('#r-chips input[name="r-chip"]').forEach(radio => {
    radio.addEventListener("change", async () => {
        const R = parseFloat(radio.value);
        if (rHidden) rHidden.value = radio.value;
        drawGraph(R);
    });
});

drawGraph(1);
