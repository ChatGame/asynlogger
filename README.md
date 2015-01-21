# InitLogger
//Log file dir
AsynLogger.setPath(FileHandler_.getInstance_(context).getFileDirByType(DataDir.LOG));
//Max log file size
AsynLogger.setMax_size(20*1024);
//max log file count
AsynLogger.setMax_files(3);
#Log
AsynLogger.getInstance().log(message);
