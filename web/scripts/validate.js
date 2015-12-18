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
    var tr = input.parentNode.parentNode;
    var inputs = tr.getElementsByTagName('input');
    var colCount = inputs.length;
    var ok = inputs[colCount-1];
    for (var i = 0; i < colCount-1; i++) {
        if ((inputs[i].value == null) || (inputs[i].value == "")) {
            hide_button(ok, "enter field '" + inputs[i].id + "' !");
            return false;
        }
        else if (inputs[i].id == "age") {
            var isInt = ( parseInt(inputs[i].value) == inputs[i].value );
            var isValid = ( isInt && (inputs[i].value >= 18) && (inputs[i].value < 100) );
            if (!isInt) {
                hide_button(ok, "'age' must be integer!'");
                return false;
            }
            else if (!isValid) {
                hide_button(ok, "incorrect 'age'!'\n 18<=age<100");
                return false;
            }
        }
    }
    ok.disabled = false;
    return true;
}
