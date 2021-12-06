#!/usr/bin/env python

import copy

def score(board, draws):
    unmarked = [item for sublist in board for item in sublist]
    for draw in draws:
        if draw in unmarked:
            unmarked.remove(draw)
    return sum(unmarked) * draws[-1]

def wins(board, draws):
    for row in board:
        all = True
        for val in row:
            if val not in draws:
                all = False
        if all:
            return score(board, draws)
    rboard = list(zip(*reversed(board)))
    for row in rboard:
        all = True
        for val in row:
            if val not in draws:
                all = False
        if all:
            return score(board, draws)
    return None



def calc_boards():
    lines = [line for line in open('input4.txt')]
    draws = [int(i) for i in lines[0].split(',')]
    num_boards = (len(lines)-1)/6
    boards = []
    for i in range(0, num_boards):
        board = lines[2 + i * 6:2 + i * 6 + 5]
        board = [[int(i) for i in line.split()] for line in board]
        boards.append(board)
    return (draws, boards, num_boards)

def part1():
    (draws, boards, num_boards) = calc_boards()
    num_draws = 1
    while True:
        chosen_draws = draws[0:num_draws]
        for board in boards:
            score = wins(board, chosen_draws)
            if (score != None):
                print(score, board)
                return
        num_draws += 1

def part2():
    (draws, boards, num_boards) = calc_boards()
    num_draws = 1
    won = {}
    while True:
        chosen_draws = draws[0:num_draws]
        for board in boards:
            score = wins(board, chosen_draws)
            if (score != None):
                won[str(board)]=True
                if len(won) == len(boards):
                    print(score, board)
                    return
        num_draws += 1

if __name__ == '__main__':
    part1()
    part2()
