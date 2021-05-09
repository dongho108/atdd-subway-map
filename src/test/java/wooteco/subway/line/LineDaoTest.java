package wooteco.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.StationDao;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("노선 이름, 색을 입력하면 노선을 저장하고 id를 반환한다")
    @Test
    void save() {
        String name = "2호선";
        String color = "red";
        Line line = new Line(name, color);
        assertThat(lineDao.save(line)).isInstanceOf(Long.class);
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void findAll() {
        String name = "2호선";
        String color = "green";
        Line line = new Line(name, color);
        lineDao.save(line);

        String name2 = "3호선";
        String color2 = "orange";
        Line line2 = new Line(name2, color2);
        lineDao.save(line2);

        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("노선 id를 통해 노선에 포함된 역의 id들을 조회한다")
    @Test
    void findStationsIdByLineId() {
        String station1 = "강남역";
        String station2 = "잠실역";
        String station3 = "신림역";
        long stationId1 = stationDao.save(station1);
        long stationId2 = stationDao.save(station2);
        long stationId3 = stationDao.save(station3);

        String name = "2호선";
        String color = "green";
        Line line = new Line(name, color);
        long lineId = lineDao.save(line);

        Section section = new Section(lineId, stationId1, stationId2);
        Section section2 = new Section(lineId, stationId2, stationId3);

        sectionDao.save(section);
        sectionDao.save(section2);

        assertTrue(
            lineDao.findStationsIdByLineId(lineId).containsAll(Arrays.asList(stationId1, stationId2, stationId3)));
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        String name = "2호선";
        String color = "green";
        Line newLine = new Line(name, color);
        long lineId = lineDao.save(newLine);

        Line line = lineDao.findById(lineId);

        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @DisplayName("노선의 이름과 색상을 수정한다")
    @Test
    void update() {
        String name = "2호선";
        String color = "green";
        Line newLine = new Line(name, color);
        long lineId = lineDao.save(newLine);

        String newName = "3호선";
        String newColor = "orange";
        Line updatedLine = new Line(newName, newColor);
        assertEquals(1, lineDao.update(lineId, updatedLine));

        Line line = lineDao.findById(lineId);
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    @DisplayName("존재하지 않는 노선을 수정한다")
    @Test
    void updateException() {
        String name = "2호선";
        String color = "green";
        Line newLine = new Line(name, color);

        String newName = "3호선";
        String newColor = "orange";
        Line updatedLine = new Line(newName, newColor);

        assertEquals(0, lineDao.update(10, updatedLine));
    }

    @DisplayName("id로 노선을 삭제한다")
    @Test
    void delete() {
        String name = "2호선";
        String color = "green";
        Line newLine = new Line(name, color);
        lineDao.save(newLine);

        String name2 = "3호선";
        String color2 = "orange";
        Line newLine2 = new Line(name2, color2);
        long lineId = lineDao.save(newLine2);

        assertEquals(1, lineDao.delete(lineId));
        assertThat(lineDao.findAll().size()).isEqualTo(1);
    }

    @DisplayName("존재하지 않는 id로 노선을 삭제한다")
    @Test
    void deleteException() {
        String name = "2호선";
        String color = "green";
        Line newLine = new Line(name, color);
        lineDao.save(newLine);

        assertEquals(0, lineDao.delete(10));
    }
}