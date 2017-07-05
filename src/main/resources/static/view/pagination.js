define(['../script/tms','jquery'],function(module,$){

    module.factory('paginationService',function(){

        //get page data function
        var getPageData = function(pageObject,page){
            pageObject.currentPage = page;
            if(pageObject.totalPage==0||pageObject.totalPage==null){
                pageObject.totalPage=1;
            }
            if (pageObject.currentPage > 1 && pageObject.currentPage < pageObject.totalPage) {
                pageObject.pages = [
                    pageObject.currentPage - 1,
                    pageObject.currentPage,
                    pageObject.currentPage + 1
                ];
            } else if (pageObject.currentPage == 1 && pageObject.totalPage == 1) {
                pageObject.pages = [
                    1
                ];
            } else if (pageObject.currentPage == 1 && pageObject.totalPage == 2) {
                pageObject.pages = [
                    1,2
                ];
            } else if (pageObject.currentPage == 1 && pageObject.totalPage > 2) {
                pageObject.pages = [
                    pageObject.currentPage,
                    pageObject.currentPage + 1,
                    pageObject.currentPage + 2
                ];
            } else if (pageObject.currentPage == pageObject.totalPage && pageObject.totalPage == 1) {
                pageObject.pages = [
                    1
                ];
            } else if (pageObject.currentPage == pageObject.totalPage && pageObject.totalPage == 2) {
                pageObject.pages = [
                    1,2
                ];
            } else if (pageObject.currentPage == pageObject.totalPage && pageObject.totalPage > 2) {
                pageObject.pages = [
                    pageObject.currentPage - 2,
                    pageObject.currentPage - 1,
                    pageObject.currentPage
                ];
            }
        };

        var service = {
            //click to the last page
            upPageClick: function(pageObject,page){
                if(pageObject.currentPage == 1){
                    return;
                };
                pageObject.currentPage --;
                getPageData(pageObject,page);
            },
            //click to the next page
            downPageClick: function(pageObject,page){
                if(pageObject.currentPage >= pageObject.totalPage){
                    return;
                };
                pageObject.currentPage ++;
                getPageData(pageObject,page);
            },
            //show the first page content
            showFirstPageContent: function(pageObject,page){
                pageObject.currentPage = 1;
                getPageData(pageObject,page);
            },
            //show the last page content
            showLastPageContent: function(pageObject,page){
                pageObject.currentPage = pageObject.totalPage;
                getPageData(pageObject,page);
            },
            //show the current page content
            showCurrentPageContent: function(pageObject,page){
                pageObject.currentPage = page;
                getPageData(pageObject,page);
            }
        };
        return service;
    });


    module.directive('pagination',function(paginationService){
        return {
            restrict: 'A',
            replace: true,
            scope: {
                pageObject:'='
            },
            templateUrl: '../view/pagination.html',
            link: function(scope,element,attrs){
                //选择页
                scope.selectPage = function(page){
                    scope.pageObject.currentPage = page;
                    scope.checkLimit(scope.pageObject.currentPage);
                };

                //上下翻页
                scope.changePage = function(operation){
                    if(operation == 'next'){
                        scope.pageObject.currentPage = (scope.pageObject.currentPage+1) > scope.pageObject.totalPage ? scope.pageObject.currentPage : (scope.pageObject.currentPage+1);
                    } else if(operation == 'prev'){
                        scope.pageObject.currentPage = (scope.pageObject.currentPage-1) < 1 ? scope.pageObject.currentPage : (scope.pageObject.currentPage-1);
                    }
                    scope.checkLimit(scope.pageObject.currentPage);
                };


                //选择首页
                scope.selectFirstPage = function(){
                    scope.pageObject.currentPage = 1;
                    scope.checkLimit(scope.pageObject.currentPage);
                };

                //选择尾页
                scope.selectLastPage = function(){
                    scope.pageObject.currentPage = scope.pageObject.totalPage == 0 ? 1 : scope.pageObject.totalPage;
                    scope.checkLimit(scope.pageObject.currentPage);
                };

                scope.upLimit = 0;
                scope.downLimit = scope.pageObject.pageSize + 1;
                scope.checkLimit = function(currentPage){
                    currentPage -= 1;
                    scope.upLimit = parseInt(currentPage / scope.pageObject.pageSize) * scope.pageObject.pageSize;
                    scope.downLimit = (parseInt(currentPage / scope.pageObject.pageSize) + 1) * scope.pageObject.pageSize + 1;
                };
            }
        };
    });

});