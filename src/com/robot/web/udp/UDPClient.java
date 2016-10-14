package com.robot.web.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient extends UDPAgent {
	
	public UDPClient() throws SocketException {
		super();
	}
	
	@Override
	public void onReceivePacket(DatagramPacket packet) throws UnknownHostException, IOException, InterruptedException {
		// notify server about receive
		sendPacket("RECEIVED", packet.getAddress(), packet.getPort());
	}
	
	@Override
	public void onTimeout() throws SocketException {
		// client has no timeout setting at present
	}

	public static void main(String[] args) {
		
		try {
			UDPClient client = new UDPClient();
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (true) {
							client.sendPacket("ONLINE_B", InetAddress.getByName("10.222.48.56"), 8044);
							Thread.sleep(5000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}).start();
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (true) {
							client.receivePacket(1024);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}).start();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			String line = reader.readLine();
			while (line != null && !"exit".equals(line)) {
				// send Message to other client
				client.sendPacket(line, InetAddress.getByName("10.222.48.56"), 8044);
				line = reader.readLine();
			}
			
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
