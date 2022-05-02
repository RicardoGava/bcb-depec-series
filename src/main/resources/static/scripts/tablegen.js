var SERIE;
var HOST;

const GET = {
    method: 'GET',
    redirect: 'follow'
};

let catchSerie = () => {
    HOST = location.protocol + '//' + location.host;
    return fetch(`${HOST}/serie`, GET)
        .then(response => response.text())
        .catch(error => console.log('error', error));
}

let receivedData = async () => {
    SERIE = await catchSerie();
    document.title = `Banco Central - Dados Abertos - Série ${SERIE}`;
    fetch(`${HOST}/${SERIE}/dados`, GET)
        .then(response => response.json())
        .then(result => genTables(result))
        .catch(error => console.log('error', error));
};

let genTables = (result) => {
    const table = document.getElementById('table');
    table.innerHTML = `<caption>Série ${SERIE}</caption>
    <thead><tr>${genTh(result[0])}</tr></thead><tbody>`;
    result.forEach((obj) => {
        var row = document.createElement('TR');
        for (key in obj) {
            var col = document.createElement('TD');
            col.setAttribute('data-label', key);
            row.appendChild(col).innerHTML = obj[key];
        }
        table.appendChild(row);
    });
    table.innerHTML += '</tbody>';
}

function genTh(result) {
    var th = '';
    for (key in result) {
        th += `<th scope="col">${key}</th>`;
    }
    return th;
}

window.addEventListener("load", receivedData);