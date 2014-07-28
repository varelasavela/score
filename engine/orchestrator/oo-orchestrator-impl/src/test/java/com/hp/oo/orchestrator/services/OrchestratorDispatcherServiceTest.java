package com.hp.oo.orchestrator.services;

import com.hp.oo.engine.node.entities.WorkerNode;
import com.hp.oo.engine.node.services.WorkerNodeService;
import com.hp.oo.engine.queue.entities.ExecutionMessage;
import com.hp.oo.orchestrator.entities.Message;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: kravtsov
 * Date: 24/07/14
 * Time: 08:58
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OrchestratorDispatcherServiceTest {

    @InjectMocks
    private OrchestratorDispatcherService orchestratorDispatcherService = new OrchestratorDispatcherServiceImpl();

    @Mock
    WorkerNodeService workerNodeService;

    @Mock
    private com.hp.oo.engine.queue.services.QueueDispatcherService queueDispatcher;

    @Mock
    private SplitJoinService splitJoinService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void simpleDispatchTest(){
        List<Message> messages = new ArrayList<>();
        messages.add(new ExecutionMessage());

        String newBulkNumber = "2";
        String uuid = "123";

        WorkerNode node = new WorkerNode();
        node.setBulkNumber("1");

        when(workerNodeService.readByUUID(anyString())).thenReturn(node);

        orchestratorDispatcherService.dispatch(messages, newBulkNumber, uuid);

        Mockito.verify(queueDispatcher, times(1)).dispatch(anyList());
        Mockito.verify(workerNodeService, times(1)).updateBulkNumber(uuid, newBulkNumber);
    }

    @Test
    public void firstDispatchTest(){
        List<Message> messages = new ArrayList<>();
        messages.add(new ExecutionMessage());

        String newBulkNumber = "2";
        String uuid = "123";

        WorkerNode node = new WorkerNode(); //node with NULL bulk number since it is first dispatch

        when(workerNodeService.readByUUID(anyString())).thenReturn(node);

        orchestratorDispatcherService.dispatch(messages, newBulkNumber, uuid);

        Mockito.verify(queueDispatcher, times(1)).dispatch(anyList());
        Mockito.verify(workerNodeService, times(1)).updateBulkNumber(uuid, newBulkNumber);
    }

    @Test
    public void sameBulkDispatchTest(){
        List<Message> messages = new ArrayList<>();
        messages.add(new ExecutionMessage());

        String newBulkNumber = "1";
        String uuid = "123";

        WorkerNode node = new WorkerNode();
        node.setBulkNumber("1");

        when(workerNodeService.readByUUID(anyString())).thenReturn(node);

        orchestratorDispatcherService.dispatch(messages, newBulkNumber, uuid);

        Mockito.verify(queueDispatcher, times(0)).dispatch(anyList());
        Mockito.verify(workerNodeService, times(0)).updateBulkNumber(uuid, newBulkNumber);
    }

    @Configuration
    static class EmptyConfig {

    }

}