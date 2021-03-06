package blog.posts.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import blog.configuration.ApplicationConfiguration;
import blog.configuration.ApplicationConfigurationStore;
import blog.configuration.RequestConfiguration;
import blog.posts.models.Post;
import blog.posts.repositories.DynamoDBPostRepository;

public class SavePost implements RequestStreamHandler {

    private ApplicationConfigurationStore applicationConfigurationStore = new ApplicationConfigurationStore();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        ObjectMapper mapper = new ObjectMapper();
        Post post = mapper.readValue(input, Post.class);
        logger.log("post:\n" + post + "\n");

        RequestConfiguration requestConfiguration = post.getRequestConfiguration();
        logger.log("requestConfiguration:\n" + requestConfiguration + "\n");

        ApplicationConfiguration applicationConfiguration = applicationConfigurationStore
                .getApplicationConfiguration(requestConfiguration);
        logger.log("applicationConfiguration:\n" + applicationConfiguration + "\n");

        DynamoDBPostRepository postRepo = new DynamoDBPostRepository(applicationConfiguration);
        Post savedPost = postRepo.save(post);
        logger.log("savedPost:\n" + savedPost + "\n");

        mapper.writeValue(output, savedPost);
    }
}