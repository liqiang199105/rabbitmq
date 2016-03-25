BoBo RabbitMQ

## rabbitmq 使用场景一 ：广播推送
## 配置文件：liveshow/conf-online/h0[0-12].config.[0-12].xml

## Consumer: HostServer
```
com.netease.liveshow.hostserver.HostServer.java  // main入口
    ExecutorService ayncService = Executors.newFixedThreadPool(3);
    ayncService.execute(new Runnable() {
	    @Override
		public void run() {
		    RabbitMQListener listener = new RabbitMQListener(config.getName());
			listener.start();
		}
	});
```
由于MQ是采取堵塞式的方式启动，所以需要用异步线程来启动他们
```
import com.netease.liveshow.hostserver.mq.RabbitMQListener.java
channel.exchangeDeclare(exchange, "direct");
String queueName = channel.queueDeclare().getQueue();
channel.queueBind(queueName, exchange, hostServerName); //routingkey
```

## Producer: CentralServer
```
com.netease.liveshow.centralserver.ctrl.MessageDispatchController.java // Http API入口
com.netease.liveshow.centralserver.service.DefaultMessageDispatchService.java
public void dispatch(OnlineBoardcastMessage message) {
		List<String> hosts = hostServerManagerService.getAllHosts();
		for (String host : hosts) {
			try {
				if(logger.isDebugEnabled()){
					logger.debug("start publish: {} {}", host, message.toJsonString());
				}
				channel.basicPublish(exchangeName, host, null, message
						.toJsonString().getBytes("UTF-8"));
				if (logger.isDebugEnabled()) {
					logger.debug("CentralServer发布全体广播到MQ, route:[" + host
							+ "], message:[" + message.toJsonString() + "]");
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("无法支持的字符串转换", e);
			} catch (IOException e) {
				logger.error("CentralServer发布全体广播到MQ出现异常, route:[" + host
						+ "], message:[" + message.toJsonString() + "]", e);
			}
		}
	}
```
#### 参考 : [RabbitMq Management Online](http://123.58.176.106:15672/#/exchanges/%2F/liveshow-route-exchange)
		