package com.robot.web.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public abstract class UDPAgent {

	private DatagramSocket socket;

	public UDPAgent() throws SocketException {
		socket = new DatagramSocket();
	}
	
	public UDPAgent(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	public void setSocketTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}

	public void receivePacket(int byteCount) throws IOException, InterruptedException {
		try {
			byte[] recvBuf = new byte[byteCount];
			DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
			socket.receive(recvPacket);
			System.out.println("Receive: " + new String(recvPacket.getData()).trim() + " -> from " + recvPacket.getAddress() + ":" + recvPacket.getPort());
			onReceivePacket(recvPacket);
		} catch (SocketTimeoutException e) {
			System.out.println("Timeout occured");
			onTimeout();
		}
	}

	public abstract void onReceivePacket(DatagramPacket packet) throws UnknownHostException, IOException, InterruptedException;

	public abstract void onTimeout() throws SocketException, UnknownHostException, IOException;
	
	public void sendPacket(String msg, InetAddress address, int port) throws IOException {
		byte[] sendBuf = msg.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
		socket.send(sendPacket);
		System.out.println("Send: " + msg);
	}

}
