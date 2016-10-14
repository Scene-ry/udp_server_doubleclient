package com.robot.web.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPServer extends UDPAgent {

	private String ClientA_IP, ClientB_IP;
	private int ClientA_PORT, ClientB_PORT;
	
	private String sender;

	public UDPServer(int port) throws SocketException {
		super(port);
		System.out.println("Server started with port " + port);
	}

	/*
	 * the format should be like that: ONLINE_A or SEND_FROM:A_TO:B_BL|HLCUTA1160841210
	 */
	@Override
	public void onReceivePacket(DatagramPacket packet) throws UnknownHostException, IOException {
		String packetMsg = new String(packet.getData()).trim();
		String[] infos = packetMsg.split("_");

		if ("ONLINE".equals(infos[0])) {
			switch (infos[1]) {
			case "A":
				ClientA_IP = packet.getAddress().toString().substring(1);
				ClientA_PORT = packet.getPort();
				System.out.println("ClientA online -> IP: " + ClientA_IP + " Port: " + ClientA_PORT);
				break;
			case "B":
				ClientB_IP = packet.getAddress().toString().substring(1);
				ClientB_PORT = packet.getPort();
				System.out.println("ClientB online -> IP: " + ClientB_IP + " Port: " + ClientB_PORT);
				break;
			}
		} else if ("OFFLINE".equals(infos[0])) {
			switch (infos[1]) {
			case "A":
				ClientA_IP = null;
				ClientA_PORT = 0;
				System.out.println("ClientA offline");
				break;
			case "B":
				ClientB_IP = null;
				ClientB_PORT = 0;
				System.out.println("ClientB offline");
				break;
			}
			
		} else if ("SEND".equals(infos[0])) {
			sender = infos[1].split(":")[1];
			String receiver = infos[2].split(":")[1];
			switch (receiver) {
			case "A":
				setSocketTimeout(5000);
				sendPacket(infos[3], InetAddress.getByName(ClientA_IP), ClientA_PORT);
				break;
			case "B":
				setSocketTimeout(5000);
				sendPacket(infos[3], InetAddress.getByName(ClientB_IP), ClientB_PORT);
				break;
			}
		} else if ("RECEIVED".equals(packetMsg)) {
			setSocketTimeout(0);
		}
	}

	@Override
	public void onTimeout() throws UnknownHostException, IOException {
		switch (sender) {
		case "A":
			setSocketTimeout(0);
			sendPacket("MSG_SEND_FAILED", InetAddress.getByName(ClientA_IP), ClientA_PORT);
			break;
		case "B":
			setSocketTimeout(0);
			sendPacket("MSG_SEND_FAILED", InetAddress.getByName(ClientB_IP), ClientB_PORT);
			break;
		}
	}
	
	public static void main(String[] args) {

		try {
			UDPServer server = new UDPServer(8044);

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (true) {
							server.receivePacket(1024);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}).start();
			
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

	}

}
