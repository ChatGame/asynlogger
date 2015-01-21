package com.handwin.asynlog.test;

import java.io.File;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.palmwin.asynlog.AsynLogger;

public class TestWriteLog extends AndroidTestCase {

	public void testWriteLog(){
		File path=new File(Environment.getExternalStorageDirectory(),"asynlog");
		AsynLogger.setPath(path.getAbsolutePath());
		AsynLogger.setMax_size(300);
		AsynLogger.getInstance().clearLog();
		AsynLogger.setMax_files(3);
		writeLog("12345",10,1000,1);
		assertFiles(new String[]{"chatgame_0.log"});
		writeLog("12345",10,1000,2);
		assertFiles(new String[]{"chatgame_0.log","chatgame_1.log"});
		writeLog("12345",10,1000,200);
		assertFiles(new String[]{"chatgame_0.log","chatgame_1.log","chatgame_2.log"});
		writeLog("12345",10,1000,400);
		assertFiles(new String[]{"chatgame_1.log","chatgame_2.log","chatgame_3.log"});
		AsynLogger.getInstance().pause();
		writeLog("12345",10,1000,1000);
		assertFiles(new String[]{"chatgame_1.log","chatgame_2.log","chatgame_3.log"});
		AsynLogger.getInstance().resume();
		sleep(1000);
		assertFiles(new String[]{"chatgame_4.log","chatgame_2.log","chatgame_3.log"});
	}
	private void writeLog(String str,int times,long sleep,long writeSleep){
		for(int i=0;i<times;i++){
			AsynLogger.getInstance().log(str);
			sleep(writeSleep);
		}
		sleep(sleep);
	}
	private void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void assertFiles(String[] files){
		File path=new File(Environment.getExternalStorageDirectory(),"asynlog");
		File[] logFiles=path.listFiles();
		assertEquals(files.length, logFiles.length);
		for(String fileName:files){
			boolean find=false;
			for(File logFile:logFiles){
				if(logFile.getName().equals(fileName)){
					find=true;
					break;
				}
			}
			assertTrue(find);
		}
	}
}
