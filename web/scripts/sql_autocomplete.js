var autoSQL = {};
autoSQL = function() {
	this.field = null;
}
autoSQL.prototype = {
	init:function(sql_id) {
		this.field = document.getElementById(sql_id);
		var sql_from = document.getElementById('sql_from');
		var del = document.createElement('input');
		del.innerHTML = '<input type="button" onclick="del_record(1)" value="del">edit</input>';
		sql_from.appendChild(del);
		if(!this.field) {
			alert("Wrong input !");
		} else {
			this.loop();
		}
	},
	loop:function() {
		var list = "";
		var value = this.field.value;
		var numOfQueries = queries.length;
		if (value.slice(-1) == " ") {
			for (var i = 0; i < numOfQueries; i++) {
				if (value.toLowerCase() == queries[i].substr(0, value.length-1).toLowerCase()) {
					list += '<a href="javascript:auto_sql.setQuery(\'' + queries[i] + '\');">' +queries[i] + '</a>'
				}
			}
		}
		if(list != "") {
			this.showHelper(list);
		} else {
			this.hideHelper();
		}
	},

	showHelper:function(list) {
		var sql_from = document.getElementById('sql_from');
		sql_from.appendChild(list);
	},

	hideHelper:function() {
	},

	setQuery:function(query) {
		this.field.value = query;
		this.hideHelper();
	},
}
	
var auto_sql = new autoSQL();

var queries = [
	"select * from",
	"insert into tabName (col1, ...) values (\"val1\", ...)",
	"delete from tabName where",
	"update tabName set col1=\"va1\", ...",
	"create database",
	"drop database",
	"use"
	];