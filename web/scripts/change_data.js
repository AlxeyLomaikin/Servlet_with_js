
function highlight_tr (button) {
    var tr = button.parentNode.parentNode;
    if (tr.className == 'active') {
        tr.className = '';
    }
    else {
        tr.className = 'active';
    }
}

function form_submit() {
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
    var fieldCount = fields.length - 1;

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

    for (var i=1; i < fieldCount; i++){
        var field = document.createElement('input');
        field.innerHTML = "<input type=hidden name='" + columns[i].textContent +
            "' value='" + fields[i].firstChild.value + "'> </input>";
        form.appendChild(field.firstChild);
    }
    form_submit();
}

function edit_record(id){
    var tr = document.getElementById('tr' + id);
    if (tr != null) {
        var fields = tr.getElementsByTagName('td');
        var childNum = fields.length;
        var colNames = document.getElementsByTagName('th');
        for (var i = 1; i < childNum-1; i++){
            fields[i].innerHTML = "<td><input required type=text id='" + colNames[i].textContent + "' value='" +
                fields[i].textContent + "' onchange='check_input(this)'> </input></td>";
        }
        var td = document.createElement('td');
        var ok = document.createElement('input');
        ok.innerHTML = "<input type=button onclick='save_record(this)' disabled value='OK'> </input>";
        var cancel = document.createElement('input');
        cancel.innerHTML = "<input type=button onclick='form_submit()' value='cancel'> </input>";
        td.appendChild(ok.firstChild);
        td.innerHTML+="&nbsp&nbsp&nbsp";
        td.appendChild(cancel.firstChild);
        tr.replaceChild(td, fields[childNum-1]);
    }
}

function add_record(add_button){
    var tr = add_button.parentNode.parentNode;
    if (tr != null) {
        var fields = tr.getElementsByTagName('td');
        var colNames = document.getElementsByTagName('th');
        var childNum = fields.length ;
        for (var i = 1; i < childNum-1; i++){
            fields[i].innerHTML = "<td><input required type=text id='" + colNames[i].textContent +
                "' onchange='check_input(this)'> </input></td>";
        }
        var td = document.createElement('td');
        var ok = document.createElement('input');
        ok.innerHTML = "<input type=button onclick='save_record(this)' disabled value='OK'> </input>";
        var cancel = document.createElement('input');
        cancel.innerHTML = "<input type=button onclick='form_submit()' value='cancel'> </input>";
        td.appendChild(ok.firstChild);
        td.appendChild(cancel.firstChild);
        tr.replaceChild(td, fields[childNum-1]);
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