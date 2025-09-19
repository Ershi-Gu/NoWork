# 切换到rocketmq源码bin目录下执行下述目录，分别启动namesrv和broker

# 启动 NameServer 并把日志输出到 ~/namesrv.log
nohup sh mqnamesrv > ~/namesrv.log 2>&1 &
# 启动 Broker 并把日志输出到 ~/broker.log
nohup sh mqbroker -n localhost:9876 -c ../conf/broker.conf > ~/broker.log 2>&1 &

# 验证进程是否启动成功
ps -ef | grep NamesrvStartup
ps -ef | grep BrokerStartup
# 查看端口占用
netstat -tulnp | grep 9876   # NameServer 默认端口
netstat -tulnp | grep 10911  # Broker 默认客户端端口

# 查看日志
tail -f ~/namesrv.log
tail -f ~/broker.log

# 控制面板启动命令
docker run -d --name rocketmq-dashboard -e "JAVA_OPTS=-Drocketmq.namesrv.addr=ip:9876" -p 19876:8082 -t apacherocketmq/rocketmq-dashboard:latest

