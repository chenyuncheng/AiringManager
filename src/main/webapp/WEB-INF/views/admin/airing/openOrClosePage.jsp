<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">

    function openDevice2(id) {
        $.post('${path }/airing/open', {
            id : id
        }, function(result) {
            result = $.parseJSON(result);
            if (result.success) {
                var div = document.getElementById(id+"_status");
                div.style.backgroundColor="#00ee00";
                $('#'+id+'_status').html('<div style="padding-top:10% ">开启</div>');
            }
        }, 'text');


    }

    function closeDevice2(id) {
        $.post('${path }/airing/close', {
            id : id
        }, function(result) {
            var div = document.getElementById(id+"_status");
            div.style.backgroundColor="#9C9C9C";
            $('#'+id+'_status').html('<div style="padding-top:10% ">关闭</div>');
        }, 'text');
    }


</script>
<div class="easyui-layout" data-options="fit:true,border:false">
    <div data-options="region:'center',border:true,title:'设备列表'">
        <c:forEach items="${list }" var="item">
            <div style="float:left;width:130px;height: 130px;margin: 10px 2px 10px 20px;border:1px solid #00a0e9;border-radius: 15px; ">

                <div style="height: 20%;width:100%;text-align: center;border-bottom:1px solid #00a0e9;padding-top: 5px;">${item.deviceName }</div>
                <div style="height: 50%;text-align: center;">
                    <c:if test="${item.status eq '1'}">
                        <div id="${item.id}_status" style="height: 100%;font-size: 24px;background-color: #00ee00;">
                            <div style="padding-top:10% ">开启</div>
                        </div>
                    </c:if>
                    <c:if test="${item.status eq '0'}">
                        <div id="${item.id}_status" style="height: 100%;font-size: 24px;background-color: #9C9C9C">
                            <div style="padding-top:10% ">关闭</div>
                        </div>
                    </c:if>
                </div>
                <div style="height: 20%;text-align: center;border-top:1px solid #00a0e9;">
                    <div style="float: left;width: 50%">
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'glyphicon-ok icon-green',plain:true" onclick="openDevice2(${item.id});">打开</a>
                    </div>
                    <div style="float: left;width: 50%">
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'glyphicon-remove icon-red',plain:true" onclick="closeDevice2(${item.id});">关闭</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
