<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    var airlingDataGrid;
    $(function() {
        airlingDataGrid = $('#airlingDataGrid').datagrid({
            url : '${path }/airing/dataGrid',
            //queryParams : getSearchConditions(),
            striped : true,
            rownumbers : true,
            pagination : true,
            singleSelect : true,
            idField : 'id',
            sortName : 'id',
            sortOrder : 'asc',
            pageSize : 20,
            pageList : [ 10, 20, 30, 40, 50, 100, 200, 300, 400, 500 ],
            frozenColumns : [ [ {
                width : '100',
                title : 'id',
                field : 'id',
                sortable : true
            }, {
                width : '280',
                title : '设备名称',
                field : 'deviceName',
                sortable : true
            } ,{
                width : '80',
                title : 'COM编号',
                field : 'comNum',
                sortable : true
            } ,{
                width : '80',
                title : '设备编号',
                field : 'deviceNum',
                sortable : true
            } ,{
                width : '60',
                title : '状态',
                field : 'status',
                sortable : true,
                formatter : function(value, row, index) {
                    switch (value) {
                        case 0:
                            return '关闭';
                        case 1:
                            return '开启';
                    }
                }
            }, {
                field : 'action',
                title : '操作',
                width : 300,
                formatter : function(value, row, index) {
                    var str = '';
                    <shiro:hasPermission name="/airing/open">
                    str += $.formatString('<a href="javascript:void(0)" class="role-easyui-linkbutton-ok" data-options="plain:true,iconCls:\'glyphicon-ok icon-green\'" onclick="openDevice(\'{0}\');" >打开</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/airing/close">
                    str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                    str += $.formatString('<a href="javascript:void(0)" class="role-easyui-linkbutton-no" data-options="plain:true,iconCls:\'glyphicon-remove icon-red\'" onclick="closeDevice(\'{0}\');" >关闭</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/airing/edit">
                    str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                    str += $.formatString('<a href="javascript:void(0)" class="role-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'glyphicon-pencil icon-blue\'" onclick="editAiringFun(\'{0}\');" >编辑</a>', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/airing/delete">
                    str += '&nbsp;&nbsp;|&nbsp;&nbsp;';
                    str += $.formatString('<a href="javascript:void(0)" class="role-easyui-linkbutton-del" data-options="plain:true,iconCls:\'glyphicon-trash icon-red\'" onclick="deleteAiringFun(\'{0}\');" >删除</a>', row.id);
                    </shiro:hasPermission>
                    return str;
                }
            } ] ],
            onLoadSuccess:function(data){
                $('.role-easyui-linkbutton-ok').linkbutton({text:'打开'});
                $('.role-easyui-linkbutton-no').linkbutton({text:'关闭'});
                $('.role-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.role-easyui-linkbutton-del').linkbutton({text:'删除'});
            },
            toolbar : '#roleToolbar'
        });
    });

    function addAiringFun() {
        parent.$.modalDialog({
            title : '添加',
            width : 500,
            height : 300,
            href : '${path }/airing/addPage',
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = airlingDataGrid;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#airingAddForm');
                    f.submit();
                }
            } ]
        });
    }

    function editAiringFun(id) {
        if (id == undefined) {
            var rows = airlingDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {
            airlingDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.modalDialog({
            title : '编辑',
            width : 500,
            height : 300,
            href : '${path }/airing/editPage?id=' + id,
            buttons : [ {
                text : '确定',
                handler : function() {
                    parent.$.modalDialog.openner_dataGrid = airlingDataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#airingEditForm');
                    f.submit();
                }
            } ]
        });
    }

    function deleteAiringFun(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = airlingDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            airlingDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要删除当前设备？', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/airing/delete', {
                    id : id
                }, function(result) {
                    result = $.parseJSON(result);
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        airlingDataGrid.datagrid('reload');
                    }
                    progressClose();
                }, 'text');
            }
        });
    }

    function openDevice(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = airlingDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            airlingDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要打开当前设备？', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/airing/open', {
                    id : id
                }, function(result) {
                    result = $.parseJSON(result);
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        airlingDataGrid.datagrid('reload');
                    }
                    progressClose();
                }, 'text');
            }
        });
    }

    function closeDevice(id) {
        if (id == undefined) {//点击右键菜单才会触发这个
            var rows = airlingDataGrid.datagrid('getSelections');
            id = rows[0].id;
        } else {//点击操作里面的删除图标会触发这个
            airlingDataGrid.datagrid('unselectAll').datagrid('uncheckAll');
        }
        parent.$.messager.confirm('询问', '您是否要关闭当前设备？', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/airing/close', {
                    id : id
                }, function(result) {
                    result = $.parseJSON(result);
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        airlingDataGrid.datagrid('reload');
                    }
                    progressClose();
                }, 'text');
            }
        });
    }

    function searchAiringFun() {
        airlingDataGrid.datagrid('load', $.serializeObject($('#searchAiringForm')));
    }
    function cleanAiringFun() {
        $('#searchAiringForm input').val('');
        airlingDataGrid.datagrid('load', {});
    }

    //加条件
    function getSearchConditions() {
        var comNum = $("#comNum").val().trim();
        var deviceNum = $("#deviceNum").val().trim();
        var status = $("#status option:selected").val().trim();
        var param = [];
        if(comNum!=null){
            param.push({
                "name" : "comNum",
                "value" : comNum
            });
        }
        if(deviceNum!=null){
            param.push({
                "name" : "deviceNum",
                "value" : deviceNum
            });
        }
        if(status!=null){
            param.push({
                "name" : "status",
                "value" : status
            });
        }
        var conditions = $.toJSON(param);
        return conditions;
    }


</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'north',border:false" style="height: 30px; overflow: hidden;background-color: #fff">
        <form id="searchAiringForm">
            <table>
                <tr>
                    <th>COM编号:</th>
                    <td><input id="comNum" name="comNum" placeholder="请输入COM编号"/></td>
                    <th>设备编号:</th>
                    <td><input id="deviceNum" name="deviceNum"  class="easyui-numberspinner" style="width: 140px; height: 29px;" required="required" data-options="editable:true"/></td>
                    <th>状态</th>
                    <td >
                        <select id="status" name="status" class="easyui-combobox" data-options="width:140,height:29,editable:false,panelHeight:'auto'">
                            <option value="3">请选择</option>
                            <option value="0">关闭</option>
                            <option value="1">打开</option>
                        </select>
                    </td>
                    <td>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'glyphicon-search',plain:true" onclick="searchAiringFun();">查询</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'glyphicon-remove-circle',plain:true" onclick="cleanAiringFun();">清空</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div data-options="region:'center',border:true,title:'设备列表'">
        <table id="airlingDataGrid" data-options="fit:true,border:false"></table>
    </div>
</div>
<div id="roleToolbar" style="display: none;">
    <shiro:hasPermission name="/airing/add">
        <a onclick="addAiringFun();" href="javascript:void(0);" class="easyui-linkbutton" data-options="plain:true,iconCls:'glyphicon-plus icon-green'">添加</a>
    </shiro:hasPermission>
</div>