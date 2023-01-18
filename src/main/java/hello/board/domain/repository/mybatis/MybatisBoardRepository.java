package hello.board.domain.repository.mybatis;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.repository.BoardRepository;
import hello.board.domain.repository.BoardSerachCond;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class MybatisBoardRepository implements BoardRepository {

    private final BoardMapper boardMapper;

    public MybatisBoardRepository(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    public Integer countTotalBoard(Criteria criteria) {
        return boardMapper.countTotalBoard(criteria);
    }

    @Override
    public void updateViewCount(long id) {
        boardMapper.updateViewCount(id);
    }

    @Override
    public Board findById(Long id) {
        return boardMapper.findById(id);
    }

    @Override
    public List<Board> findAll(BoardSerachCond cond) {
        return boardMapper.findAll(cond);
    }

    @Override
    public List<Board> findPagedBoard(Criteria criteria) {
        return boardMapper.findPagedBoard(criteria);
    }

    @Override
    public List<Board> findByWriter(String writer) {
        return boardMapper.findByWriter(writer);
    }

    @Override
    public Board save(Board board) {
        board.setRegedate(LocalDateTime.now());
        board.setUpdateDate(LocalDateTime.now());
        int row = boardMapper.save(board);
        if (row != 1) return null;
        return board;
    }

    @Override
    public Board update(Long id, Board updateParam) {
        updateParam.setUpdateDate(LocalDateTime.now());
        int row = boardMapper.update(id, updateParam);
        if (row != 1) return null;
        return findById(id);
    }

    // 필요시 반환
    @Override
    public void delete(Long id) throws RuntimeException {
        int row = boardMapper.delete(id);
    }
}
