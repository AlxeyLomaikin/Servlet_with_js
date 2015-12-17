
function highlight_tr (id) {
    var tr = document.getElementById('tr' + id);
    if (tr.className == 'active') {
        tr.className = '';
    }
    else {
        tr.className = 'active';
    }
}

function submit_changes() {
    var form = document.getElementById('sql_form');
    if (form!=null) {
        form.submit();
    }
}

function save_record(ok_button) {
    var tr = ok_button.parentNode.parentNode;
    var id = tr.id.substring(2);
    var fields = tr.getElementsByTagName('td');
    var columns = document.getElementsByTagName('th');
    var fieldCount = tr.childElementCount - 1;

    var form = document.getElementById('sql_form');

    if (id != "") {
        var edit = document.createElement('input');
        edit.innerHTML = "<input type=hidden name='edit_id' value='" + id + "'> </input>";
        form.appendChild(edit.firstChild);
    }
    else{
        var add = document.createElement('input');
        add.innerHTML = "<input type=hidden name='add' value='true'> </input>";
        form.appendChild(add.firstChild);
    }

    for (var i=1; i<fieldCount; i++){
        var field = document.createElement('input');
        field.innerHTML = "<input type=hidden name='" + columns[i].textContent +
            "' value='" + fields[i].firstChild.value + "'> </input>";
        form.appendChild(field.firstChild);
    }
    submit_changes();
}

function edit_record(id){
    var tr = document.getElementById('tr' + id);
    if (tr != null) {
        var fields = tr.getElementsByTagName('td');
        var childNum = tr.childElementCount;
        var colNames = document.getElementsByTagName('th');
        for (var i = 1; i<childNum-1; i++){
            fields[i].innerHTML = "<td><input type=text id='" + colNames[i].textContent + "' value='" +
                fields[i].textContent + "' onchange='check_input(this)'> </input></td>";
        }
        fields[childNum-1].innerHTML = "<td><input type=button onclick='save_record(this)' value='OK'> </input></td>";
    }
}

function add_record(add_button){
    var tr = add_button.parentNode.parentNode;
    if (tr != null) {
        var fields = tr.getElementsByTagName('td');
        var colNames = document.getElementsByTagName('th');
        var childNum = tr.childElementCount;
        for (var i = 1; i<childNum-1; i++){
            fields[i].innerHTML = "<td><input type=text id='" + colNames[i].textContent +
                "' onchange='check_input(this)'> </input></td>";
        }
        fields[childNum-1].innerHTML = "<td><input type=button onclick='save_record(this)' value='OK'> </input></td>";
    }
}


function del_record(id){
    var del_id = document.createElement('input');
    del_id.innerHTML = '<input type="hidden" name="del_id" value="' + id + '">del_id</input>';
    var form = document.getElementById('sql_form');
    if(form!=null) {
        form.appendChild(del_id.firstChild);
        form.submit();
    }
}