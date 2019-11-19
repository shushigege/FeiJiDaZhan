package com.example.feijidazhan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.Vector;

class my {//新建一个类 里面东西是静态的 当全局变量用
    public static int js = 0;//击杀数
    public static int w,h;//屏幕的宽高
    public static float bili;//比例，用于适应不同的屏幕

    public static Vector<hj> list = new Vector<hj>();//所有飞行物的集合，添加到这个类才能被画出来
    public static Vector<hj> drlist = new Vector<hj>();//敌人飞机的集合，添加到这个集合才能被子弹打中
    public static Bitmap myhj,drhj,bj,myzd;//图片，我的灰机 敌人灰机 背景 我的子弹
    public static myhj my;//我的灰机
    public static bj b;//背景
        }

public class jjp extends View {//画
    private Paint p = new Paint();//画笔
    private float x,y;//按下屏幕时的坐标
    private float myx,myy;


    public jjp(Context context){
        super(context);
        //添加事件控制玩家飞机
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN){
                    x = e.getX();
                    y = e.getY();
                    myx = my.my.r.left;
                    myy = my.my.r.top;
                }
                float xx=myx+e.getX()-x;
                float yy=myy+e.getY()-y;
                //我的飞机不能飞出屏幕
                 xx=xx<my.w-my.my.w/2?xx:my.w-my.my.w/2;
                 xx=xx>-my.my.w/2?xx:-my.my.w/2;
                 yy=yy<my.h-my.my.h/2?yy:my.h-my.my.h/2;
                 yy=yy>-my.my.h/2?yy:-my.my.h/2;
                 my.my.setX(xx);
                 my.my.setY(yy);
                 return true;
            }
        });


        setBackgroundColor(Color.WHITE);//背景颜色设置为黑色
        my.myhj =  BitmapFactory.decodeResource(getResources(),R.mipmap.hj);//加载图片
        my.drhj = BitmapFactory.decodeResource(getResources(),R.mipmap.dr);
        my.myzd = BitmapFactory.decodeResource(getResources(),R.mipmap.zd);
        my.bj = BitmapFactory.decodeResource(getResources(), R.mipmap.bj);
        new Thread(new re()).start();//新建一个线程 让画布自动重绘
         new Thread(new loaddr()).start();//新建一个 加载敌人的线程
    }

    @Override
    protected void onDraw(Canvas g){
        super.onDraw(g);
        g.drawBitmap(my.b.img,null,my.b.r,p);//画背景

        for(int i=0;i<my.list.size();i++){
           hj h = my.list.get(i);
           g.drawBitmap(h.img,null,h.r,p);
        }
        g.drawText("击杀："+my.js,0,my.h-50,p);
    }

    @Override
    protected void onSizeChanged(int w,int h,int oldw,int oldh){
        super.onSizeChanged(w,h,oldw,oldh);

        my.w=w;//获取宽
        my.h=h;//高
        // 获取手机（应该不是手机的吧 是这控件的吧）分辨率和1920*1080的比例
        // 然后飞机的宽高乘上这个分辨率就能在不同大小的屏幕正常显示了
        // 为什么用1920*1080呢 因为我手机就是这个分辨率。。。
        my.bili= (float) (Math.sqrt(my.w * my.h)/ Math.sqrt(1920 * 1080));
        p.setTextSize(50*my.bili);//设置字体大小，“击杀”的大小
        p.setColor(Color.WHITE);//设为白色
        // 好了 到这里游戏开始了
        my.b = new  bj();
        my.my = new myhj();
    }

    private class re implements Runnable{
        @Override
        public void run(){
            while (true){
                try{
                    Thread.sleep(10);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                postInvalidate();
            }
        }
    }

    private class loaddr implements Runnable{

        @Override
        public void run(){
            while (true){
                try{
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                try {
                    new drhj();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}

class hj{
    public RectF r = new RectF();//这个是用来确定位置的
    public int hp;//生命
    public float w,h;//宽高
    public Bitmap img;//图片

    public void setX(float x){
        r.left = x;
        r.right = x+w;
    }
    public void setY(float y){
        r.top = y;
        r.bottom = y+h;
    }

    public boolean pengzhuang(hj obj,float px){
        px*=my.bili;//凡是涉及到像素的 都乘一下分辨率比例
        if (r.left+px - obj.r.left <= obj.w && obj.r.left - this.r.left+px <= this.w-px-px)
            if (r.top+px - obj.r.top <= obj.h && obj.r.top - r.top+px <= h-px-px) {
            return true;
        }
        return false;
    }
}

class bj extends hj implements Runnable{
    public bj(){
        w = my.w;
        h = my.h*2;
        img = my.bj;
        setX(-my.h);
        new Thread(this).start();
    }
    @Override
    public void run(){
        //这里控制背景向下移
        while (true){
            try{
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            if (r.top+2<=0){
                setY(r.top+2);
            }else{
                setY(-my.h);
            }
        }
    }
}

class drhj extends hj implements Runnable{
    private long sd0 = (long)(Math.random()*10)+10;
    public drhj(){
        w=h=200*my.bili;
        setX((float)(Math.random()*(my.w-w)));//x是随机的
        setY(-h);//在屏幕外刚好看不到的位置
        img = my.drhj;
        hp = 12;
        my.list.add(this);//添加到集合里，才能被画出来
        my.drlist.add(this);//添加到敌人集合 添加到这个集合才能被子弹打到
        new Thread(this).start();
    }

    @Override
    public void run(){
        while(hp>0){
            try{
                Thread.sleep(sd0);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            setY(r.top+2*my.bili);
            if(r.top>=my.h)
                break;//敌人飞出屏幕，跳出循环
        }
        //从集合删除
        my.list.remove(this);
        my.drlist.remove(this);
    }
}

class myhj extends hj implements Runnable{
    public myhj(){
        w=h=200*my.bili;//凡是涉及到像素的 都乘一下分辨率比例my.bili
        // 设置初始位置
        setX(my.w/2-w/2);
        setY(my.h*0.7f-h/2);
        img=my.myhj;//初始化图片
        //     my.list.add(this);//添加到集合里 这样才能被画出来
        new Thread(this).start();//发射子弹的线程
    }

    public void run(){
        while (true){
            //90毫秒发射一颗子弹
            try{
                Thread.sleep(90);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            new myzd(this);
        }

    }

}

class myzd extends hj implements Runnable{
    private int dps;
    private float sd0;

    public myzd(hj hj){
        w=h=90*my.bili;//凡是涉及到像素的 都乘一下分辨率比例my.bili
        img=my.myzd;//图片
        sd0=6*my.bili;//速度=6
        dps=6;//伤害=6        //设在玩家中心的偏上一点
        setX(hj.r.left+hj.w/2-w/2);
        setY(hj.r.top-h/2);
        my.list.add(this);//添加到集合里 这样才能被画出来
        new Thread(this).start();//新建一个子弹向上移动的线程
    }

    @Override
    public void run(){
        boolean flag = false;
        while(true){
            try{
                Thread.sleep(5);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            setY(r.top-sd0);//向上移sd0像素

            try{
                for (int i=0;i<my.drlist.size();i++){
                    hj h = my.drlist.get(i);
                    if (pengzhuang(h,30)){
                        h.hp = -dps;
                        flag = true;
                        my.js++;
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
            if (flag || r.top+h<=0)
                break;
        }
        my.list.remove(this);
    }
}

