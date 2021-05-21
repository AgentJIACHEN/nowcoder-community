package com.nowcoder.community;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.util.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.concurrent.Future;

//@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MyUploadFile {

    private static final Logger logger = LoggerFactory.getLogger(MyUploadFile.class);

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String shareBucketName;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private String imageStorage="D:\\Desktop\\新建文件夹 (3)";
    private String fileName="wo";
    private String suffix=".jpg";

    @Test
    public void uploadFile() {

        // 启用定时器,监视该图片,一旦生成了,则上传至七牛云.
        MyUploadFile.UploadTask task = new MyUploadFile.UploadTask(fileName, suffix);
        Future future = taskScheduler.scheduleAtFixedRate(task, 1000);
        task.setFuture(future);
        sleep(10000);
    }

    class UploadTask implements Runnable {

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;
        // 启动任务的返回值
        private Future future;
        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {

            // 上传失败
            if (uploadTimes >= 3) {
                logger.error("上传次数过多,终止任务:" + fileName);
                future.cancel(true);
                return;
            }

            String path = imageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                logger.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                // 设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(0));
                // 生成上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                // 指定上传机房
                UploadManager manager = new UploadManager(new Configuration(Zone.zone2()));
                try {
                    // 开始上传图片
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/" + suffix, false);
                    // 处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("0")) {
                        logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                    } else {
                        logger.info(String.format("第%d次上传成功[%s].", uploadTimes, fileName));
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                }
            } else {
                logger.error("没有该文件:" + fileName + ",程序运行结束。");
                future.cancel(true);
                return;
            }
        }
    }

    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
