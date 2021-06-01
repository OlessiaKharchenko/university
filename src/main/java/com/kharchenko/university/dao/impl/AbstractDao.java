package com.kharchenko.university.dao.impl;

import com.kharchenko.university.dao.GenericDao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T> implements GenericDao<T, Integer> {
    protected RowMapper<T> mapper;
    protected final JdbcTemplate jdbcTemplate;

    public AbstractDao(RowMapper<T> mapper, JdbcTemplate jdbcTemplate) {
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<T> getAll() {
        return jdbcTemplate.query(getStatementCreatorForGetAll(getQueryToGetAll()), mapper);
    }

    @Override
    public Optional<T> getById(Integer id) {
        try {
            return jdbcTemplate.queryForStream(getStatementCreatorForGetById(getQueryToGetById(), id), mapper)
                    .findFirst();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        return jdbcTemplate.update(getQueryToDeleteById(), id) > 0;
    }

    @Override
    public void addAll(List<T> list) {
        jdbcTemplate.batchUpdate(getQueryToAddAll(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement statement, int i) throws SQLException {
                T entity = list.get(i);
                fillRow(statement, entity);
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    private PreparedStatement getScrollableStatement(Connection connection, String query) throws SQLException {
        return connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
    }

    private PreparedStatementCreator getStatementCreatorForGetAll(String query) {
        return connection -> getScrollableStatement(connection, query);
    }

    protected PreparedStatementCreator getStatementCreatorForGetById(String query, Integer id) {
        return connection -> {
            PreparedStatement statement = getScrollableStatement(connection, query);
            statement.setInt(1, id);
            return statement;
        };
    }

    protected abstract String getQueryToGetAll();

    protected abstract String getQueryToDeleteById();

    protected abstract String getQueryToGetById();

    protected abstract String getQueryToAddAll();

    protected abstract void fillRow(PreparedStatement statement, T entity) throws SQLException;
}