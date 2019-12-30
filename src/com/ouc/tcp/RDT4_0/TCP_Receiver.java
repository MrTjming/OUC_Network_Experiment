/***************************2.1: ACK/NACK*****************/
/***** Feng Hong; 2015-12-09******************************/
package com.ouc.tcp.RDT4_0;

import com.ouc.tcp.client.TCP_Receiver_ADT;
import com.ouc.tcp.message.TCP_PACKET;

public class TCP_Receiver extends TCP_Receiver_ADT {
	
	private TCP_PACKET ackPack;	//回复的ACK报文段
	int sequence = 1;//用于记录当前待接收的包序号，注意包序号不完全是
	int count = 0;
	ReceiveWindow window;
		
	/*构造函数*/
	public TCP_Receiver() {
		super();	//调用超类构造函数
		super.initTCP_Receiver(this);	//初始化TCP接收端
		window=new ReceiveWindow(client);
	}

	@Override
	//接收到数据报：检查校验和，设置回复的ACK报文段
	public void rdt_recv(TCP_PACKET recvPack) {
				
		//检查校验码，生成ACK		
		if(CheckSum.computeChkSum(recvPack) == recvPack.getTcpH().getTh_sum()) {
			//生成ACK报文段（设置确认号）
			tcpH.setTh_ack(recvPack.getTcpH().getTh_seq());
			ackPack = new TCP_PACKET(tcpH, tcpS, recvPack.getSourceAddr());
			tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
			ackPack.setTcpH(tcpH);
			//回复ACK报文段
			reply(ackPack);

			// 加内容加到缓存区
			window.addRecvPacket(recvPack);

		}else{
			System.out.println("校验和不匹配,传输错误");
		}

	}

	@Override
	//交付数据（将数据写入文件）；不需要修改
	public void deliver_data() {
//		//检查dataQueue，将数据写入文件
//		File fw = new File("recvData.txt");
//		BufferedWriter writer;
//
//
//		try {
//			writer = new BufferedWriter(new FileWriter(fw, true));
//
//			//循环检查data队列中是否有新交付数据
//			while(!dataQueue.isEmpty()) {
//				int[] data = dataQueue.poll();
//
//				if (count == 0 ){
//					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//					String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
//					writer.write("start: "+date+"\n");
//
//				}
//
//				//将数据写入文件
//				for(int i = 0; i < data.length; i++) {
//					writer.write(data[i] + "\n");
//				}
//				count = count + data.length;
//
//				if (count==100000){//100000
//					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//					String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
//					writer.write("end: "+date+"\n");
//
//				}
//
//
//				writer.flush();		//清空输出缓存
//			}
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	//回复ACK报文段,不需要修改
	public void reply(TCP_PACKET replyPack) {
		//设置错误控制标志
		tcpH.setTh_eflag((byte)7);	//eFlag=0，信道无错误
				
		//发送数据报
		client.send(replyPack);
	}
	
	
	
}
