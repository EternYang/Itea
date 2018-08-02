package com.itea.server;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/socketServer")
@Component
public class SocketServer {

	private static final Logger log = Logger.getLogger(SocketServer.class);
	
	private static Session session;
	
	@OnOpen
	public void open(Session session){
		SocketServer.session = session;
		log.info("client is connect...............................");
	}
	
	@OnClose
	public void close(){
		log.info("the connection is close......................");
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnMessage
	public void message(String msg){
		log.info("get Message--->" + msg);
	}
	
	@OnError
	public void error(Session session, Throwable error){
		error.printStackTrace();
	}
	
	public static void send_message(String message){
		if (session != null){
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
