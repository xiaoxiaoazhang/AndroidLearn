package com.chihun.learn.retrofitdemo.network;

public class Urls {

    public static final String BASE_URL = "http://image.baidu.com/";

    //百度图片分类图片api
//    以GET形式提交，返回JSON
//
//    URL：http://image.baidu.com/data/imgs?col=&tag=&sort=&pn=&rn=&p=channel&from=1
//
//    参数：col=大类&tag=分类&sort=0&pn=开始条数&rn=显示数量&p=channel&from=1
//
//    PS：sort可以为0和1，作用。。未知
//
//    例子：http://image.baidu.com/data/imgs?col=美女&tag=小清新&sort=0&pn=10&rn=10&p=channel&from=1
    public static final String GET_IMAGE = "data/imgs";

//    分类api
// 1、明星
//
//    http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=明星&tag2=全部&ie=utf8
//
//    http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=明星&tag2=全部&ftags=女明星&ie=utf8
//
//    http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=明星&tag2=全部&ftags=女明星##内地&ie=utf8

//    2、美女
//
//    http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=美女&tag2=全部&ie=utf8
//
//    http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=美女&tag2=全部&ftags=小清新&ie=utf8

//    http://image.baidu.com/search/detail?z=0&word=%E9%99%88%E6%BC%AB&hs=0&pn=11&spn=0&di=0&pi=54291685469&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cs=2712670358%2C2718806202&os=&simid=&adpicid=0&lpn=0&fm=&sme=&cg=&bdtype=-1&oriquery=&objurl=http%3A%2F%2Ff.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Fb3b7d0a20cf431ad968f7fad4736acaf2fdd98c0.jpg&fromurl=&gsm=0&catename=pcindexhot&islist=&querylist=
}
