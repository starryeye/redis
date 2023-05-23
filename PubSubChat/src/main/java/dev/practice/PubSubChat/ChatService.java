package dev.practice.PubSubChat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class ChatService implements MessageListener {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Message: " + message.toString());
    }

    /**
     * chatRoomName 은 ChannelTopic 이름이다.
     *
     * 채팅방 입장 (=ChannelTopic 구독)
     * - ListenerContainer 에 Listener(this) 등록 -> 메시지가 push 되면 onMessage() 메서드가 Redis 에 의해 호출된다.(Subscribe)
     * - 사용자의 입력을 기다리고 종료 조건 검사
     * - 종료 조건이 아닌 메시지는 Redis 에 메시지를 Publish 한다.
     * - ListenerContainer 에 Listener(this) 제거
     */
    public void enterChatRoom(String chatRoomName) {
        //chatRoom 이라는 ChannelTopic Listener 등록
        redisMessageListenerContainer.addMessageListener(this, new ChannelTopic(chatRoomName));

        System.out.printf("Chat Room(%s) entered..%n", chatRoomName);

        Scanner in = new Scanner(System.in);

        while (in.hasNextLine()) {

            String line = in.nextLine();

            if(line.equals("q")) {
                System.out.println("Exit..");
                break;
            }

            redisTemplate.convertAndSend(chatRoomName, line);
        }

        //Listener 제거
        redisMessageListenerContainer.removeMessageListener(this);
    }
}
