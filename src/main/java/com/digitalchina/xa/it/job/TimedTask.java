package com.digitalchina.xa.it.job;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import com.digitalchina.xa.it.dao.TConfigDAO;
import com.digitalchina.xa.it.dao.WalletAccountDAO;
import com.digitalchina.xa.it.dao.WalletTransactionDAO;
import com.digitalchina.xa.it.model.WalletTransactionDomain;
import com.digitalchina.xa.it.service.TConfigService;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.TConfigUtils;

import scala.util.Random;

@Component
public class TimedTask {
	@Autowired
    private WalletAccountDAO walletAccountDAO;
	@Autowired
	private WalletTransactionDAO walletTransactionDAO;

	@Autowired
	private TConfigDAO tconfigDAO;
	
	
/*	@Autowired
	private SigninRewardService srService;*/

	@Transactional
	@Scheduled(cron="10,40 * * * * ?")
	public void updateTranscationStatus(){
		Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
		List<WalletTransactionDomain> wtdList = walletTransactionDAO.selectHashAndAccounts();
		if(wtdList == null) {
			web3j.shutdown();
			return;
		}
		try {
			for(int i = 0; i < wtdList.size(); i++) {
				String transactionHash = wtdList.get(i).getTransactionHash();
				System.out.println("WALLET定时任务_链上未确认_transactionHash:" + transactionHash);
				TransactionReceipt tr = web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get().getResult();
				if(tr == null) {
					System.out.println(transactionHash + "仍未确认，查询下一个未确认交易");
					continue;
				}
				if(!tr.getBlockHash().contains("00000000")) {
					String accountFrom = wtdList.get(i).getAccountFrom();
					String accountTo = wtdList.get(i).getAccountTo();
					
					BigInteger balanceFrom = web3j.ethGetBalance(accountFrom,DefaultBlockParameterName.LATEST).send().getBalance();
					BigInteger balanceTo = web3j.ethGetBalance(accountTo,DefaultBlockParameterName.LATEST).send().getBalance();
					
					BigInteger gasUsed = tr.getGasUsed();
					BigInteger blockNumber = tr.getBlockNumber();
					WalletTransactionDomain wtd = new WalletTransactionDomain();
					wtd.setTransactionHash(transactionHash);
					wtd.setBalanceFrom(Double.parseDouble(balanceFrom.toString()));
					wtd.setBalanceTo(Double.parseDouble(balanceTo.toString()));
					wtd.setGas(Double.valueOf(gasUsed.toString()));
					wtd.setConfirmBlock(Integer.valueOf(blockNumber.toString()));
					wtd.setStatus(1);
					walletTransactionDAO.updateByTransactionHash(wtd);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			web3j.shutdown();
		}
	}

	
	//检查区块链节点工作状态是否正常
	@Transactional
	@Scheduled(cron="5 * * * * ?")
	public void checkEthNodes(){
		String textAddress = "0x8a950e851344715a51036567ca1b44aab3f15110";
		List<String> ipArr = TConfigUtils.selectIpArr();
		for(int index = 0; index < ipArr.size(); index++) {
			Web3j web3j =Web3j.build(new HttpService(ipArr.get(index)));
			try {
				web3j.ethGetBalance(textAddress,DefaultBlockParameterName.LATEST).send().getBalance();
				tconfigDAO.UpdateEthNodesStatus(ipArr.get(index), true);
	        } catch (IOException e) {
	        	if(e.getMessage().contains("Failed to connect to")) {
	        		System.out.println(e.getMessage());
	        		tconfigDAO.UpdateEthNodesStatus(ipArr.get(index), false);
	        	}
			}
		}
	}
}
