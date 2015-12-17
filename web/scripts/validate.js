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

function hide_button(button, message){
    alert(message);
    button.disabled = true;
}

function check_input(input) {
    var ok = input.parentNode.parentNode.lastChild.firstChild;
    if ((input.value == null) || (input.value == "")) {
        hide_button(ok, "enter field '" + input.id + "' !");
        return false;
    }
    else if (input.id == "age") {
        var isInt = ( parseInt(input.value) == input.value );
        var isValid = ( isInt && (input.value>=18) && (input.value<100) );
        if (!isInt){
            hide_button(ok, "'age' must be integer!'");
            return false;
        }
        else if (!isValid){
            hide_button(ok, "incorrect 'age'!'\n 18<=age<100");
            return false;
        }
        else{
            ok.disabled = false;
            return true;
        }
    }
    else
        ok.disabled = false;
    return true;
}
