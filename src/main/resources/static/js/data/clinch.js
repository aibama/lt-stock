var pageCurr;
var form;
$(function() {
    layui.use('table', function(){
        var table = layui.table;
        form = layui.form;
        tableIns=table.render({
            elem: '#realClinchList',
            url:'/real/clinchList',
            cellMinWidth: 80,
            page: true,
            request: {
                pageName: 'pageNum', //页码的参数名称，默认：pageNum
                limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
            },
            response:{
                statusName: 'code', //数据状态的字段名称，默认：code
                statusCode: 200, //成功的状态码，默认：0
                countName: 'totals', //数据总数的字段名称，默认：count
                dataName: 'list' //数据列表的字段名称，默认：data
            },
            cols: [[{
                    type: 'checkbox'
                }, {
                    field: 'stockCode',title: '股票代码',align:'center'
                }, {
                    field: 'stockName',title: '股票名称',align:'center'
                }, {
                    field: 'nowPrice',title: '当前价格',sort: true,align:'center'
                }, {
                    field: 'rose',title: '涨幅',sort: true,align:'center'
                }, {
                    field: 'exchange',title: '换手率',sort: true,align:'center'
                }, {
                    field: 'volamount',title: '成交次数',align:'center'
                }, {
                    field: 'dealRmb',title: '成交金额',align:'center'
                }, {
                    field: 'dealTime',title: '成交时间',align:'center'
                }, {
                    field: 'operate',title: '操作',toolbar: '#operateTpl',unresize: true
                }]],
            done: function(res, curr, count){
                pageCurr=curr;
                $("#spanCount").html("共有数据："+count+" 条");
            }
        });

        //监听工具条
        table.on('tool(realClinchTable)', function(obj){
            var data = obj.data;
            if(obj.event === 'remove'){
                //删除
                alert(data.stockName)
            } else if(obj.event === 'edit'){
                //编辑
                alert("编辑");
            }else if(obj.event === 'recover'){
                //查看
                alert("查看");;
            }
        });

        //监听提交
        form.on('submit(userSubmit)', function(data){
            formSubmit(data);
            return false;
        });
    });

    //搜索框
    layui.use(['form'], function(){
        var form = layui.form;
        //TODO 数据校验
        //监听搜索框
        form.on('submit(sreach)', function(data){
            //重新加载table
            load(data);
            return false;
        });
    });
});

function load(obj){
    //重新加载table
    tableIns.reload({
        where:
        obj.field,
        page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

function delUser(obj,id,name) {
    var currentUser=$("#currentUser").html();
    if(null!=id){
        if(currentUser==id){
            layer.alert("对不起，您不能执行删除自己的操作！");
        }else{
            layer.confirm('您确定要删除'+name+'用户吗？', {
                btn: ['确认','返回'] //按钮
            }, function(){
                $.post("/user/updateUserStatus",{"id":id,"status":0},function(data){
                    if (data.code == 1) {
                        layer.alert(data.msg,function(){
                            layer.closeAll();
                            load(obj);
                        });
                    } else {
                        layer.alert(data.msg);
                    }
                });
            }, function(){
                layer.closeAll();
            });
        }
    }
}