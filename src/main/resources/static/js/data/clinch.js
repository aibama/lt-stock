layui.use(['layer','table','jquery'], function(){
    var table = layui.table,
        form = layui.form,
        $ = layui.jquery;;
    var tableIns = table.render({
        elem: '#realClinchList',
        url:'/real/clinchBriefList',
        cellMinWidth: 80,
        page: true,
        request: {
            pageName: 'pageNum', //页码的参数名称，默认：pageNum
            limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
        },
        response:{
            statusCode: 200 //成功的状态码，默认：0
        },
        cols: [[{
            type: 'checkbox'
        }, {
            field: 'stockCode',title: '股票代码',align:'center'
        }, {
            field: 'stockName',title: '股票名称',align:'center'
        }, {
            field: 'rose',title: '涨幅',sort: true,align:'center'
        }, {
            field: 'exchange',title: '换手率',sort: true,align:'center'
        }, {
            field: 'volamount',title: '成交次数',align:'center'
        }, {
            field: 'dealNumSum',title: '成交量',align:'center'
        }, {
            field: 'dealDate',title: '成交时间',align:'center'
        }, {
            field: 'operate',title: '操作',toolbar: '#operateTpl',unresize: true
        }]],
        done: function(res, curr, count){
            $("#spanCount").html("共有数据："+count+" 条");
        }
    });

    //监听工具条
    table.on('tool(realClinchTable)', function(obj){
        var data = obj.data;
        if(obj.event === 'detailList'){
            //详细列表
            WeAdminEdit('详情列表','/real/clinch/detail',"stock_code:"+data.stockCode+";"+"deal_date:"+data.dealDate)
        }
    });

    //搜索框
    form.on('submit(sreach)', function(data){
        tableIns.reload({
            where:
            data.field,
            page: {
                curr: 1 //从当前页码开始
            }
        });
        return false;
    });
});

// //搜索框
// layui.use(['form'], function(){
//     var form = layui.form;
//     //TODO 数据校验
//     //监听搜索框
//     form.on('submit(sreach)', function(data){
//         //重新加载table
//         load(data);
//         return false;
//     });
// });

// function load(obj){
//     //重新加载table
//     tableIns.reload({
//         where:
//         obj.field,
//         page: {
//             curr: 1 //从当前页码开始
//         }
//     });
// }

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