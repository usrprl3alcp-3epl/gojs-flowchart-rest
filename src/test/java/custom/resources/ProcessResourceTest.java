package custom.resources;

import custom.dao.ProcessDAO;
import custom.dao.TaskDAO;
import custom.domain.Process;
import custom.dto.process.ProcessDTO;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessResourceTest {

    private static final ProcessDAO processDAO = mock(ProcessDAO.class);
    private static final TaskDAO taskDAO = mock(TaskDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ProcessResource(processDAO, taskDAO))
            .build();

    @Captor
    private ArgumentCaptor<Process> processArgumentCaptor;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        reset(processDAO, taskDAO);
    }

    @Test
    public void createProcess_daoCreatedSuccessfully_created() {
        Process process = givenProcess("Some process");
        givenDaoCreatedSuccessfully(process);
        Response response = whenCreateRequestPerform(process);
        thenCreated(response, process);
    }

    private void givenDaoCreatedSuccessfully(Process process) {
        when(processDAO.create(any(Process.class))).thenReturn(process);
    }

    private void thenCreated(Response response, Process process) {
        assertThat(response.getStatusInfo()).isEqualTo(OK);
        ProcessDTO processDTO = response.readEntity(ProcessDTO.class);
        assertThat(processDTO.getName()).isEqualTo(process.getName());
    }

    private Response whenCreateRequestPerform(Process process) {
        final Response response = resources.client().target("/processes")
                .request(APPLICATION_JSON_TYPE)
                .post(entity(process, APPLICATION_JSON_TYPE));
        return response;
    }

    private Process givenProcess(String name) {
        Process process = new Process(name);
        return process;
    }
}