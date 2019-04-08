package com.digitalchina.xa.it;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint160;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionHash;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.dao.SystemBlockInfoDAO;
import com.digitalchina.xa.it.dao.TPaidlotteryDetailsDAO;
import com.digitalchina.xa.it.dao.TPaidlotteryInfoDAO;
import com.digitalchina.xa.it.model.SystemBlockInfoDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;
import com.digitalchina.xa.it.service.TPaidlotteryService;
import com.digitalchina.xa.it.service.impl.TPaidlotteryServiceImpl;
import com.digitalchina.xa.it.util.TConfigUtils;

import rx.Subscription;
import scala.util.Random;

@SpringBootTest
public class Test {
	@Autowired
    private SystemBlockInfoDAO systemBlockInfoDAO;
	private static String[] ip = {"http://10.7.10.124:8545","http://10.7.10.125:8545","http://10.0.5.217:8545","http://10.0.5.218:8545","http://10.0.5.219:8545"};

	@org.junit.Test
	public void insertNewBlock(){

		Web3j web3j = Web3j.build(new HttpService(ip[new Random().nextInt(5)]));
		try {
			System.err.println("插入新的区块信息..........");
			DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(1);
			Block block = web3j.ethGetBlockByNumber(defaultBlockParameter, true).send().getResult();
			System.out.println(block);
			SystemBlockInfoDomain systemBlockInfoEntity = new SystemBlockInfoDomain();
			systemBlockInfoEntity.setId(block.getNumber().intValue());
			systemBlockInfoEntity.setNumber(block.getNumber().toString());
			systemBlockInfoEntity.setHash(block.getHash());
			systemBlockInfoEntity.setParentHash(block.getParentHash());
			systemBlockInfoEntity.setNonce(block.getNonce().toString());
			systemBlockInfoEntity.setSha3Uncles(block.getSha3Uncles());
			systemBlockInfoEntity.setLogsBloom(block.getLogsBloom());
			systemBlockInfoEntity.setTransactionsRoot(block.getTransactionsRoot());
			systemBlockInfoEntity.setStateRoot(block.getStateRoot());
			systemBlockInfoEntity.setReceiptsRoot(block.getReceiptsRoot());
			systemBlockInfoEntity.setAuthor(block.getAuthor());
			systemBlockInfoEntity.setMiner(block.getMiner());
			systemBlockInfoEntity.setMixHash(block.getMixHash());
			systemBlockInfoEntity.setDifficulty(block.getDifficulty().toString());
			systemBlockInfoEntity.setTotalDifficulty(block.getTotalDifficulty().toString());
			systemBlockInfoEntity.setExtraData(block.getExtraData());
			systemBlockInfoEntity.setSize(block.getSize().toString());
			systemBlockInfoEntity.setGasLimit(block.getGasLimit().toString());
			systemBlockInfoEntity.setGasUsed(block.getGasUsed().toString());
			systemBlockInfoEntity.setTimestamp(block.getTimestamp().toString());
			
			if((!block.getTransactions().equals(null)) && (block.getTransactions().size() > 0)){
				String transactions = "";
				for(TransactionResult temp : block.getTransactions()){
					TransactionObject temp1 = (TransactionObject) temp;
					transactions += temp1.getHash() + ";";
				}
				transactions = transactions.substring(0, transactions.length() - 1);
				systemBlockInfoEntity.setTransactions(transactions);
			}
			
			if((!block.getUncles().equals(null))&& block.getUncles().size() > 0){
				String uncles = "";
				for(String temp : block.getUncles()){
					uncles += temp + ";";
				}
				uncles = uncles.substring(0, uncles.length() -1 );
				systemBlockInfoEntity.setUncles(uncles);
			}

			if((!block.getSealFields().equals(null)) && block.getSealFields().size() > 0){
				String sealFields = "";
				for(String temp : block.getSealFields()){
					sealFields = temp + ";";
				}
				sealFields = sealFields.substring(0, sealFields.length() - 1);
				systemBlockInfoEntity.setSealFields(sealFields);
			}
			systemBlockInfoDAO.insert(systemBlockInfoEntity);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}