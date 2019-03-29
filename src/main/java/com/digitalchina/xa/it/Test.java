package com.digitalchina.xa.it;
//
//import javax.annotation.Resource;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.digitalchina.xa.it.controller.EthAccountController;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.util.ResultUtil;
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
public class Test {
	@Resource
	private EthAccountController eth;
	@org.junit.Test
	public void test() {
	ResultUtil result = eth.login("18438612965", "123");
	System.out.println(result.getMsg());
}
}
