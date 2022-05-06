var SERIE;
var HOST;
var firstLoad = true;

var parameters = {
    page: 0,
    size: 12,
    sort: 'data,ASC',
    dataInicial: '',
    dataFinal: ''
};
Object.seal(parameters);

const query = (p) => Object.keys(p).map(key => `${key}=${p[key]}`).join('&');

var lastValues = {
    dataInicial: '',
    dataFinal: '',
    totalPages: '',
    pageNumber: ''
}
Object.seal(lastValues);

const GET = {
    method: 'GET',
    redirect: 'follow'
};

let catchSerie = () => {
    HOST = "http://localhost:8080";
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
    console.log(query(parameters));
    fetch(`${HOST}/${SERIE.serie}/dados?${query(parameters)}`, GET)
        .then(response => response.json())
        .then(result => genTables(result))
        .catch(error => console.log('error', error));
};

let genTables = (result) => {
    lastValues.totalPages = result.totalPages;
    lastValues.pageNumber = result.pageable.pageNumber;
    if (result.content == '') {
        parameters.page = result.totalPages - 1;
        receivedData();
    }
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
                Items: ${result.pageable.offset + 1}-${Math.min(result.pageable.offset + result.pageable.pageSize, result.totalElements)} de ${result.totalElements}<br>
                <label for="pageSize">Itens por página:</label>
                <select id="pageSize" name="pageSize" onChange="pageSize(this.value);">
                  <option value="12" ${(parameters.size == 12) ? 'selected' : ''}>12</option>
                  <option value="24" ${(parameters.size == 24) ? 'selected' : ''}>24</option>
                  <option value="36" ${(parameters.size == 36) ? 'selected' : ''}>36</option>
                  <option value="48" ${(parameters.size == 48) ? 'selected' : ''}>48</option>
                  <option value="60" ${(parameters.size == 60) ? 'selected' : ''}>60</option>
                  <option value="2000" ${(parameters.size == 2000) ? 'selected' : ''}>Todos</option>
                </select>
            </td>
            <td>
                <input type="checkbox" id="fromDateCheckbox" onclick="fromDateEnable();" ${(parameters.dataInicial != '') ? 'checked' : ''}>
                <label for="fromDate" class="dates">Inicial:</label>
                <input type="date" id="fromDate" name="fromDate" value="${dateFormatInverter(parameters.dataInicial)}" onChange="fromDateFilter(this.value);" ${(parameters.dataInicial == '') ? 'disabled="true"' : ''}>
                <br>
                <input type="checkbox" id="toDateCheckbox" onclick="toDateEnable();" ${(parameters.dataFinal != '') ? 'checked' : ''}>
                <label for="toDate" class="dates">Final:</label>
                <input type="date" id="toDate" name="toDate" value="${dateFormatInverter(parameters.dataFinal)}" onChange="toDateFilter(this.value);" ${(parameters.dataFinal == '') ? 'disabled="true"' : ''}>
            </td>
            <td>
                <input type="button" value="&#9668;" onclick="previous()">
                    &nbsp;Página: <input type="number" id="page" min="1" max="${result.totalPages}" onfocus="this.select();" value="${result.pageable.pageNumber + 1}" onKeyUp="goToPage(event);"> de ${result.totalPages}&nbsp;
                <input type="button" value="&#9658;" onclick="next()">
            </td>
        </tr>
    </tfoot>`;
    document.getElementById('source').innerHTML = `Fonte: ${SERIE.fonte}`;
};

const genTh = (content) => {
    var th = '';
    for (key in content) {
        th += `<th scope="col">${(SERIE.nome != null && key == 'valor') ? SERIE.unidadePadrao : key}</th>`;
    }
    return th;
};

const fromDateFilter = (date) => {
    parameters.dataInicial = dateFormatInverter(date);
    (parameters.dataInicial != '') ? receivedData() : {};
};

const toDateFilter = (date) => {
    parameters.dataFinal = dateFormatInverter(date);
    (parameters.dataFinal != '') ? receivedData() : {};
};

const fromDateEnable = () => {
    if (document.getElementById("fromDateCheckbox").checked) {
        document.getElementById("fromDate").disabled = false;
        parameters.dataInicial = lastValues.dataInicial;
        document.getElementById("fromDate").value = dateFormatInverter(lastValues.dataInicial);
        (parameters.dataInicial != '') ? receivedData() : {};
    } else {
        document.getElementById("fromDate").disabled = true;
        lastValues.dataInicial = parameters.dataInicial;
        parameters.dataInicial = '';
        (lastValues.dataInicial != parameters.dataInicial) ? receivedData() : {};
    }
};

const toDateEnable = () => {
    if (document.getElementById("toDateCheckbox").checked) {
        document.getElementById("toDate").disabled = false;
        document.getElementById("toDate").value = dateFormatInverter(lastValues.dataFinal);
        parameters.dataFinal = lastValues.dataFinal;
        (parameters.dataFinal != '') ? receivedData() : {};
    } else {
        document.getElementById("toDate").disabled = true;
        lastValues.dataFinal = parameters.dataFinal;
        parameters.dataFinal = '';
        (lastValues.dataFinal != parameters.dataFinal) ? receivedData() : {};
    }
};

const dateFormatInverter = (date) => {
    if (date.includes('/') === true) {
        return date.split('/').reverse().join('-');
    } else if (date.includes('-') === true) {
        return date.split('-').reverse().join('/');
    } else {
        return date;
    }
}

const goToPage = (event) => {
    var page = document.getElementById("page");
    if (page.value > lastValues.totalPages) {
        page.value = lastValues.totalPages;
    } else if (page.value < 1) {
        page.value = 1;
    }
    if (event.key === "Enter") {
        parameters.page = page.value - 1;
        receivedData();
    }
}

const next = () => {
    if (lastValues.pageNumber < lastValues.totalPages - 1) {
        parameters.page++;
        receivedData();
    }
}

const previous = () => {
    if (lastValues.pageNumber > 0) {
        parameters.page--;
        receivedData();
    }
}

const pageSize = (size) => {
    parameters.size = size;
    lastValues.totalPages
    receivedData();
}

window.addEventListener("load", receivedData);