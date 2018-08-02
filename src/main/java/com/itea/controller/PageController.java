package com.itea.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 控制页面转发的controller
 * Controller to control page direct
 * @author Eternal.Y
 * @Date 2018/08/01
 * */

@Controller
@RequestMapping("/itea")
public class PageController {
	
	private static final Logger log = Logger.getLogger(PageController.class);

	@RequestMapping("/work")
	protected String work(HttpServletRequest request) {
		log.info("request work page......");
		return "work";
	}

	@RequestMapping("/inventory")
	protected String inventory(HttpServletRequest request) {
		log.info("request inventory page......");
		return "inventory";
	}

	@RequestMapping("/setting")
	protected String setting(HttpServletRequest request) {
		log.info("request setting page......");
		return "setting";
	}
	
	@RequestMapping("/main")
	public String main(HttpServletRequest request) {
		log.info("request main page......");
		return "main";
	}
	
	
	
}
