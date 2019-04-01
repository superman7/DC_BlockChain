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
import com.digitalchina.xa.it.service.TPaidlotteryService;
import com.digitalchina.xa.it.service.impl.TPaidlotteryServiceImpl;

import rx.Subscription;
import scala.util.Random;

@SpringBootTest
public class Test {
	private static String ip = "http://10.7.10.124:8545";
    static final int PUBLIC_KEY_SIZE = 64;
	static final int PUBLIC_KEY_LENGTH_IN_HEX = PUBLIC_KEY_SIZE << 1;
	private static final byte chainId = (byte) 10;
	public static void main(String[] args) {
		BigInteger turnBalance = BigInteger.valueOf(100000000000000000L);
//		创建web3j对象
		Web3j web3j = Web3j.build(new HttpService(ip));

        System.out.println("开始解锁。。。");
        
        Credentials credentials = null;
        try {
//        	创建临时文件用来解锁账户
            File file = new File("c://temp/temp.json");

            if( !file.exists() ){
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
//            将私钥写入临时文件中
            bw.write("{\"address\":\"4ead00a857f1018278a91ee166bcc70ce664b8e3\",\"id\":\"044cfcf2-96bb-477e-ab46-a8a141abf246\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"4cde6ea2a16f1935876177b37d83dc77d9dcc180502ef6bbe273b2fc0a31304a\",\"kdfparams\":{\"p\":6,\"r\":8,\"salt\":\"4c3c7b39976c17479e87c6a8637ee2bcac3140bfa61fd845687197a19c046638\",\"dklen\":32,\"n\":4096},\"cipherparams\":{\"iv\":\"4bf51a04942a1da2b846729e5bf3b7e5\"},\"kdf\":\"scrypt\",\"mac\":\"058616508a4073b90b077d4bd14036287c6550a37e307ddd71c3d861d47df797\"}}");
            bw.close();
//            利用账户密码和私钥解锁账户并生成合约
            credentials = WalletUtils.loadCredentials("mini0823", file);
            System.out.println("解锁成功。。。");
//            删除临时文件
            file.delete();
            System.out.println("删除临时keystore文件成功。。。");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CipherException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        新建转账处理器
        TransactionReceiptProcessor transactionReceiptProcessor = new NoOpProcessor(web3j);
        TransactionManager transactionManager = null;
        try{
//        	新建转账事务
            transactionManager = new RawTransactionManager(web3j, credentials, chainId, transactionReceiptProcessor);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("构建交易管理异常！");
        }
        try {
//        	发送转账请求,web3j对象,已经生成的合约,转账金额,换算格式,
//        	之后会在控制台打印出整个过程的日志,在最后一条中可以看到blockHash,转出用户,转入账户,以及转账的transactionHash等用来验证是否转账成功
			TransactionReceipt transactionReceipt = Transfer.sendFunds(
			        web3j, credentials, "0x189abcd4cb82534d9d7b2ee181b28bcc86c64853",
			        BigDecimal.valueOf(0.1), Convert.Unit.ETHER).send();
			System.out.println(transactionReceipt);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
