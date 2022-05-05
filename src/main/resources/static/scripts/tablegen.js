var SERIE;
var HOST = "http://localhost:8080"; //temporary var
var parameters = "page=0&size=20&sort=data,ASC";
var RESULT;
var firstLoad = true;

const GET = {
    method: 'GET',
    redirect: 'follow'
};

let catchSerie = () => {
    //HOST = location.protocol + '//' + location.host;
    return fetch(`${HOST}/serie`, GET)
        .then(response => response.json())
        .catch(error => console.log('error', error));
};

let receivedData = async () => {
    if (firstLoad) {
        SERIE = await catchSerie();
        document.title = `Série ${SERIE.serie}${(SERIE.nome != null) ? " - " + SERIE.nome : ""}`;
        firstLoad = false;
    }
    fetch(`${HOST}/${SERIE.serie}/dados?${parameters}`, GET)
        .then(response => response.json())
        .then(result => genTables(result))
        .catch(error => console.log('error', error));
};

let genTables = (result) => {
    RESULT = result;
    const table = document.getElementById('table');
    table.innerHTML = `<caption>${document.title}</caption>
    <thead>
        <tr>${genTh(result.content[0])}</tr>
    </thead>
    <tbody>`;
    result.content.forEach((obj) => {
        var row = document.createElement('TR');
        for (key in obj) {
            var col = document.createElement('TD');
            col.setAttribute('data-label', (SERIE.nome != null && key == 'valor') ? SERIE.unidadePadrao : key);
            row.appendChild(col).innerHTML = obj[key];
        }
        table.appendChild(row);
    });
    table.innerHTML += `</tbody>
    <tfoot>
        <tr>
            <td>
                Items: ${result.pageable.offset + 1}-${result.pageable.offset + result.pageable.pageSize} de ${result.totalElements}
            </td>
            <td>
                <input type="checkbox" id="fromDateCheckbox" onclick="fromDateEnable()">
                <label for="fromDate">Inicial:</label>
                <input type="date" id="fromDate" name="fromDate">
                <br>
                <input type="checkbox" id="toDateCheckbox" onclick="toDateEnable()">
                <label for="toDate">Final:</label>
                <input type="date" id="toDate" name="toDate">
            </td>
            <td>
                <input type="button" value="&laquo;" onclick="previous()">
                    &nbsp;Página: <input type="number" id="page" min="1" max="${result.totalPages}" onfocus="this.select();" value="${result.pageable.pageNumber + 1}" onKeyUp="goToPage(event);"> de ${result.totalPages}&nbsp;
                <input type="button" value="&raquo;" onclick="next()">
            </td>
        </tr>
    </tfoot>`;
    fromDateEnable();
    toDateEnable();
    document.getElementById('source').innerHTML = `Fonte: ${SERIE.fonte}`;
};

const genTh = (content) => {
    var th = '';
    for (key in content) {
        th += `<th scope="col">${(SERIE.nome != null && key == 'valor') ? SERIE.unidadePadrao : key}</th>`;
    }
    return th;
};

const fromDateEnable = () => {
    if (document.getElementById("fromDateCheckbox").checked) {
        document.getElementById("fromDate").disabled = false;
    } else {
        document.getElementById("fromDate").disabled = true;
    }
};
const toDateEnable = () => {
    if (document.getElementById("toDateCheckbox").checked) {
        document.getElementById("toDate").disabled = false;
    } else {
        document.getElementById("toDate").disabled = true;
    }
};

const goToPage = (event) => {
    var page = document.getElementById("page");
    if (page.value > RESULT.totalPages) {
        page.value = RESULT.totalPages;
    } else if (page.value < 1) {
        page.value = 1;
    }
    if (event.key === "Enter") {
        parameters = "page="
        parameters += page.value - 1;
        parameters += "&size=20&sort=data,ASC";
        receivedData();
    }
}

const next = () => {
    if (RESULT.pageable.pageNumber < RESULT.totalPages - 1) {
        parameters = "page="
        parameters += RESULT.pageable.pageNumber + 1;
        parameters += "&size=20&sort=data,ASC";
        receivedData();
    }
}

const previous = () => {
    if (RESULT.pageable.pageNumber > 0) {
        parameters = "page="
        parameters += RESULT.pageable.pageNumber - 1;
        parameters += "&size=20&sort=data,ASC";
        receivedData();
    }
}

window.addEventListener("load", receivedData);