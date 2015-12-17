function check_query() {
    var sql_area = document.getElementById('sql_area');
    if (sql_area && sql_area.value != '') {
        document.getElementById('send').disabled = false;
        return true;
    }
    else {
        document.getElementById('send').disabled = true;
        return false;
    }
}
function check_input(input) {
    if ( (input.value == null) || (input.value == "")) {
        alert("please input");
        return false;
    }
    return true;
}
