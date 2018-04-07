package com.example.administrator.dame;

/**
 * Created by Administrator on 2018/4/7.
 */

public class Api {

    public static final String BASE_URL1="https://www.zhaoapi.cn/product/";

    public static final String BASE_URL2="addCart?Pid=284&uid=2743&source=android";

    public static final String BASE_URL3="getProductDetail?uid=71&pid=1&source=android";

    public static String base_url="http://120.27.23.105/";
    //添加购物车
    public static String add="product/addCart";
    //查询
    public static  String select="product/getCarts";
    //更新购物车
    public  static String update="product/updateCarts";
    //删除购物车
    public static String delete="product/deleteCart";
    //创建订单
    public static String chuangjian="product/createOrder";
    //状态
    public static String zhuangtai="product/updateOrder";
    //列表
    public static String liebiao="product/getOrders";
}
