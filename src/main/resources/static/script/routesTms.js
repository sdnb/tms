define([], function(){
    return {
        defaultRoutePath: '/tms/notFound',
        routes: {
            '/': {
                templateUrl: '/view/hy.html',
                dependencies: ['../view/hyCtrl']
            },

            '/tms': {
                templateUrl: '/view/hy.html',
                dependencies: ['../view/hyCtrl']
            },

            '/tms/notFound': {
                templateUrl: '/view/notFound.html',
                dependencies: ['../view/notFoundCtrl']
            },

            '/tms/noAuthority': {
                templateUrl: '/view/noAuthority.html',
                dependencies: ['../view/noAuthorityCtrl']
            },

            '/tms/hy': {
                templateUrl: '/view/hy.html',
                dependencies: ['../view/hyCtrl']
            },

            '/tms/dhb': {
                templateUrl: '/view/dhb.html',
                dependencies: ['../view/dhbCtrl']
            },

            '/tms/ly': {
                templateUrl: '/view/ly.html',
                dependencies: ['../view/lyCtrl']
            },

            '/tms/xt': {
                templateUrl: '/view/xt.html',
                dependencies: ['../view/xtCtrl']
            },

            '/tms/xt/zcr': {
                templateUrl: '/view/xt/zcr.html',
                dependencies: ['../view/xt/zcrCtrl']
            },

            '/tms/xt/hys': {
                templateUrl: '/view/xt/hys.html',
                dependencies: ['../view/xt/hysCtrl']
            },

            '/tms/xt/gly': {
                templateUrl: '/view/xt/gly.html',
                dependencies: ['../view/xt/glyCtrl']
            },

            '/tms/xt/rz': {
                templateUrl: '/view/xt/rz.html',
                dependencies: ['../view/xt/rzCtrl']
            },

            '/tms/xt/lj': {
                templateUrl: '/view/xt/lj.html',
                dependencies: ['../view/xt/ljCtrl']
            },
        }
    };
});


