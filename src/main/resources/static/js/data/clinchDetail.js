layui.use(['layer','table','jquery'], function(){
    var table = layui.table,
        $ = layui.jquery;
    table.render({
        elem: '#realClinchDetailList',
        url:'/real/clinchDetailList',
        where:{stockCode: $("#stock_code").val(),dealDate:$("#deal_date").val()},
        cellMinWidth: 80,
        page: true,
        request: {
            pageName: 'pageNum', //页码的参数名称，默认：pageNum
            limitName: 'pageSize' //每页数据量的参数名，默认：pageSize
        },
        response:{
            statusCode: 200 //成功的状态码，默认：0
        },
        cols: [[
            {field: 'stockCode',title: '股票代码',align:'center'},
            {field: 'stockName',title: '股票名称',align:'center'},
            {field: 'rose',title: '涨幅',sort: true,align:'center'},
            {field: 'exchange',title: '换手率',sort: true,align:'center'},
            {field: 'volamount',title: '成交次数',align:'center'},
            {field: 'dealNumSum',title: '成交量',align:'center'},
            {field: 'dealTime',title: '成交时间',align:'center'}
        ]]
    })
});