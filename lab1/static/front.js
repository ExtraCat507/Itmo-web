const xs = [-3, -2, -1, 0, 1, 2, 3, 4, 5]
const rs = [1, 2, 3, 4, 5]
const scale = 30;


window.onload = function () {
    const selectorX = document.getElementById("x")
    const inputY = document.getElementById("y")
    const submitBtn = document.getElementById("submit-data")
    const fld = document.getElementById("radButtons").querySelectorAll('button')

    var lastR = 3;
    var lastX = -3, lastY;


    drawGraphics(undefined,undefined,lastR);


    fld.forEach(element => {
        element.addEventListener("click", event => {
            lastR = element.value
            fld.forEach(btn => {

                btn.className = "btnReleased"
            })
            element.className = "btnPushed"
            console.log("R chosen: ", lastR)
            drawGraphics(lastX,lastY,lastR)
        })
    });

    inputY.addEventListener("input", event => {
        if (validateY(inputY.value)) {
            lastY = inputY.value;
            inputY.className = "validInput"
            document.getElementById("errLabelForY").classList.add("hidden")
            drawGraphics(lastX,lastY,lastR)
            console.log("Y chosen: ", lastY)
            //drawDot(selectorX.value,inputY.value, lastR)
        }
        else {
            inputY.className = "invalidInput"
            document.getElementById("errLabelForY").classList.remove("hidden")
        }

    })

    selectorX.addEventListener("change", event => {
        lastX = selectorX.value;
        drawGraphics(lastX,lastY,lastR)
        console.log("x chosen: ", lastX)
    })


    submitBtn.addEventListener("click", async event => {
        let x = selectorX.value
        let y = inputY.value

        if (validate(x, y, lastR)) {
            try {
                const response = await fetch(`fcgi-bin/app.jar?x=${x}&y=${y}&r=${lastR}`)
                if (!response.ok) {
                    throw new Error(`Response status: ${response.status}`);
                }
                const result = await response.json()
                addResult(result.x, result.y, result.r, result.result,result.time)
                //console.log(result.time)

            } catch (err) {
                console.log(err)
            }
        }
        else {
            alert("Неверно выбраны данные запроса")
        }
    })



}

function validateY(input) {
    if (input.length <= 0) return false;
    input = Number(input)
    return input > -5 && input <= 5;
}

function validate(x, y, r) {
    if (xs.includes(Number(x)) && rs.includes(Number(r))) {
        if (y.length <= 0) return false;
        y = Number(y)
        return y > -5 && y <= 5
    }
    return false;
}

function addResult(x, y, r, result,time) {
    const resTable = document.getElementById("resultTable")
    const row = resTable.insertRow(1)
    row.insertCell(0).innerText = x
    row.insertCell(1).innerText = y
    row.insertCell(2).innerText = r
    row.insertCell(3).innerText = result
    row.insertCell(4).innerText = time
}

function drawGraphics(x,y,R) {

    let canvas = document.getElementById("graph");
    let ctx = canvas.getContext("2d");


    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // центр холста примем за (0,0)
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;


    ctx.fillStyle = "rgba(99, 219, 97, 0.7)";
    ctx.strokeStyle = "black";

    // --- четверть круга (верхняя левая) ---
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.arc(centerX, centerY, R * scale, -Math.PI / 2, Math.PI, true);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // --- прямоугольник (нижняя левая часть) ---
    ctx.beginPath();
    ctx.rect(centerX - (R / 2) * scale, centerY, (R / 2) * scale, R * scale);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // --- треугольник (верхняя правая часть) ---
    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(centerX + (R / 2) * scale, centerY);
    ctx.lineTo(centerX, centerY - (R / 2) * scale);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // оси координат
    ctx.beginPath();
    ctx.moveTo(0, centerY);
    ctx.lineTo(canvas.width - 5, centerY);
    ctx.moveTo(centerX, 5);
    ctx.lineTo(centerX, canvas.height);
    ctx.strokeStyle = "black";
    ctx.closePath()
    ctx.stroke();


    ctx.fillStyle = "black";
    ctx.font = "12px monospace";

    function drawTick(x, y, label) {
        ctx.beginPath();
        ctx.moveTo(x - 3, y);
        ctx.lineTo(x + 3, y);
        ctx.moveTo(x, y - 3);
        ctx.lineTo(x, y + 3);
        ctx.stroke();
        if (label) ctx.fillText(label, x + 5, y - 5);
    }
    ctx.beginPath()

    // по X
    drawTick(centerX + R * scale, centerY, R);
    drawTick(centerX + (R / 2) * scale, centerY, R / 2);
    drawTick(centerX - R * scale, centerY, -R);
    drawTick(centerX - (R / 2) * scale, centerY, -R / 2);

    // по Y
    drawTick(centerX, centerY - R * scale, R);
    drawTick(centerX, centerY - (R / 2) * scale, R / 2);
    drawTick(centerX, centerY + R * scale, -R);
    drawTick(centerX, centerY + (R / 2) * scale, -R / 2);

    ctx.moveTo(canvas.width - 1, centerY)
    ctx.lineTo(canvas.width - 6, centerY - 5)
    ctx.moveTo(canvas.width - 1, centerY)
    ctx.lineTo(canvas.width - 6, centerY + 5)

    ctx.moveTo(centerX, 1)
    ctx.lineTo(centerX - 5, 6)
    ctx.moveTo(centerX, 1)
    ctx.lineTo(centerX + 5, 6)
    ctx.closePath()
    ctx.strokeStyle = "black";
    ctx.stroke();

    // dot
    if ((x != undefined) && (y != undefined)) {
        ctx.beginPath()
        ctx.arc(centerX + x * scale, centerY - y * scale, 5, 0, 2 * Math.PI)
        ctx.closePath();
        ctx.fillStyle = "#3F453E";
        ctx.fill();
        ctx.strokeStyle = "#3F453E";
        ctx.stroke();
    }


}
