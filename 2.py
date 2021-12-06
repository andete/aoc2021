#!/usr/bin/env python

def part1():
    h = 0
    d = 0
    for movement in open('input2.txt'):
        (action, amount) = movement.split()
        amount = int(amount)
        if action == 'forward':
                h += amount
        if action == 'down':
            d += amount
        if action == 'up':
            d -= amount
    print(h, d, h * d)


def part2():
    h = 0
    d = 0
    a = 0
    for movement in open('input2.txt'):
        (action, amount) = movement.split()
        amount = int(amount)
        if action == 'forward':
                h += amount
                d += a * amount
        if action == 'down':
            a += amount
        if action == 'up':
            a -= amount
    print(h, d, a, h * d)

if __name__ == '__main__':
    part1()
    part2()
