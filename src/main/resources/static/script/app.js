requirejs.config({
    paths:{
        angular:"/script/lib/angular/angular.min",
        jquery:'/script/lib/jquery/dist/jquery.min',
        angularResource: "/script/lib/angular-resource/angular-resource.min",
        angularRoute:"/script/lib/angular-route/angular-route.min",
        bootstrap: "/script/lib/bootstrap/dist/js/bootstrap.min",
        ajaxfileupload: "/script/lib/ajaxfileupload/ajaxfileupload.min",
        md5:"/script/lib/js-md5/js/md5.min",
        bootstrapDateTimePicker:"/script/lib/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min",
        bootstrapDateTimePickerCN:"/script/lib/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN",
        flot:"/script/lib/flot/jquery.flot"
    },
    shim:{
        jquery: { exports: '$' },
        bootstrap: ['jquery'],
        angular: { exports: 'angular',deps:['jquery']},
        angularResource: ['angular'],
        angularRoute:['angular'],
        ajaxfileupload:['jquery'],
        bootstrapDateTimePicker:['bootstrap'],
        bootstrapDateTimePickerCN:['bootstrapDateTimePicker'],
        flot:['jquery']
    },
    waitSeconds:30
});


require
(
    [
        'angular','tms','../view/indexCtrl','../script/service/commonService','../script/filter/commonFilter'
    ],
    function(angular)
    {
        angular.bootstrap(document, ['tms']);
    }
);

