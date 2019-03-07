package com.wootion.commons.utils;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 串口服务类，提供打开、关闭串口，读取、发送串口数据等服务（采用单例设计模式）
 * @author zhong
 *
 */
public  class SerialTool {
    protected static Logger logger = LogManager.getLogger(SerialTool.class);

    private static SerialTool serialTool = null;

    static {
        //在该类被ClassLoader加载时就初始化一个SerialTool对象
        if (serialTool == null) {
            serialTool = new SerialTool();
        }
    }

    //私有化SerialTool类的构造方法，不允许其他类生成SerialTool对象
    private SerialTool() {}

    /**
     * 获取提供服务的SerialTool对象
     * @return serialTool
     */
    public static SerialTool getSerialTool() {
        if (serialTool == null) {
            serialTool = new SerialTool();
        }
        return serialTool;
    }


    /**
     * 查找所有可用端口
     * @return 可用端口名称列表
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> findPort() {

        //获得当前所有可用串口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

        ArrayList<String> portNameList = new ArrayList<>();

        //将可用串口名添加到List并返回该List
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }
        return portNameList;
    }

    /**
     * 打开串口
     * @param portName 端口名称
     * @param baudrate 波特率
     * @return 串口对象
     * @throws Exception 设置串口参数失败
     */
    public SerialPort openPort(String portName, int baudrate) throws Exception {

        SerialPort serialPort = null;
        try {
            //通过端口名识别端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            //打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = portIdentifier.open(portName, 2000);
            //判断是不是串口
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                try {
                    //设置一下串口的波特率等参数
                    serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    logger.info("串口参数设置已完成，波特率为"+baudrate+",数据位8bits,停止位1位,无奇偶校验");
                } catch (UnsupportedCommOperationException e) {
                    throw new Exception("设置串口参数失败");
                }
                logger.info("Open " + portName + " sucessfully !");
            }
            else {
                //不是串口
                throw new Exception("端口指向设备不是串口类型");
            }
        } catch (NoSuchPortException e1) {
            throw new Exception("没有该端口对应的串口设备");
        } catch (PortInUseException e2) {
            throw new Exception("端口已被占用");
        }
        return serialPort;
    }

    /**
     * 关闭串口
     * @param serialPort 待关闭的串口对象
     */
    public void closePort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            logger.info("Close " + serialPort + " sucessfully !");
            serialPort = null;
        }
    }


    /**
     * 往串口发送数据
     * @param serialPort 串口对象
     * @param order    待发送数据
     * @throws Exception 向串口发送数据失败
     */
    public void sendToPort(SerialPort serialPort, byte[] order) throws Exception {

        OutputStream out = null;

        try {
            out = serialPort.getOutputStream();
            out.write(order);
            out.flush();

        } catch (IOException e) {
            //throw new SendDataToSerialPortFailure();
            throw new Exception("向串口发送数据失败");
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                throw new Exception("关闭串口对象的输出流出错");
            }
        }

    }

    /**
     * 从串口读取数据
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     * @throws Exception 从串口读取数据时出错
     */
    public byte[] readFromPort(SerialPort serialPort) throws Exception {

        InputStream in = null;
        byte[] bytes = null;

        try {
            in = serialPort.getInputStream();
            int bufflenth = in.available();        //获取buffer里的数据长度

            while (bufflenth != 0) {
                bytes = new byte[bufflenth];    //初始化byte数组为buffer中数据的长度
                in.read(bytes);
                bufflenth = in.available();
            }
        } catch (IOException e) {
            throw new Exception("从串口读取数据时出错");
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch(IOException e) {
                throw new Exception("关闭串口对象输入流出错");
            }
        }
        return bytes;
    }

    /**
     * 添加监听器
     * @param port     串口对象
     * @param listener 串口监听器
     * @throws Exception 监听类对象过多
     */
    public void addListener(SerialPort port, SerialPortEventListener listener) throws Exception {

        try {
            //给串口添加监听器
            port.addEventListener(listener);
            //设置当有数据到达时唤醒监听接收线程
            port.notifyOnDataAvailable(true);
            //设置当通信中断时唤醒中断线程
            port.notifyOnBreakInterrupt(true);

        } catch (TooManyListenersException e) {
            throw new Exception("监听类对象过多");
        }
    }







    public static void main(String[] args) throws Exception {
//        ArrayList<String> arraylist=findPort();
//        int useAbleLen=arraylist.size();
//        if(useAbleLen==0)
//        {
//            System.out.println("没有找到可用的串口端口，请check设备！");
//        }
//        else
//        {
//            System.out.println("已查询到该计算机上有以下端口可以使用：");
//            for(int index=0;index<arraylist.size();index++)
//            {
//                System.out.println("该COM端口名称:"+arraylist.get(index));
//                //测试串口配置的相关方法
//                //打开com
//                SerialPort serialPort=openPort(arraylist.get(0), 57600);
//                //发送数据
//                String openStr = "010600000001";
//                String closeStr = "010600000000";
//                byte[] bstr = new BigInteger(openStr, 16).toByteArray();
//                byte[] bstr2 = new BigInteger(closeStr, 16).toByteArray();
//                openStr=openStr+String.format("%04x", CRC16Util.calcCrc16(bstr)).toUpperCase();
//                closeStr=closeStr+String.format("%04x", CRC16Util.calcCrc16(bstr2)).toUpperCase();
//                System.out.println(openStr);
//                System.out.println(closeStr);
//                sendToPort(serialPort, openStr.getBytes());
//                //关闭的串口对象
//                closePort(serialPort);
//            }
//        }

        //openAiring("COM3",1);
//        String str = "Hello Word!";//"010600000001480A";
//        byte[] n = Base64.getDecoder().decode(str);
//        System.out.println(str.getBytes());
//        SerialPort serialPort=openPort("COM3", 115200);
//        byte[] b = readFromPort(serialPort);
//        closePort(serialPort);
//        SerialTool tool = new SerialTool();
//        tool.openAiring("COM3",1);

    }


}
