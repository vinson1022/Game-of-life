package com.vinson.gameoflife

import android.graphics.Point
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CellModel(private var colAndRow: Int, private val observer: Observer<ArrayList<Cell>>) {

    private val liveCells = HashMap<String, Cell>()
    private val deadCells = HashMap<String, Cell>()
    private val periodTask : Observable<ArrayList<Cell>>

    companion object {
        const val TIME_PERIOD = 3L
    }

    init {
        initCellsObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
        periodTask = nextChangedCellsObservable()
        periodTask
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun changeCellFromUser(pos: Point) {
        val cell = getCells(pos.toKey()) ?: return

        if (cell.live) {
            cell.live = false
            liveCells.remove(cell.pos.toKey())
            deadCells[cell.pos.toKey()] = cell
        } else {
            cell.live = true
            deadCells.remove(cell.pos.toKey())
            liveCells[cell.pos.toKey()] = cell
        }

        Observable.just(ArrayList(liveCells.values))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    fun resetGameWorld() {
        liveCells.clear()
        deadCells.clear()
        initCellsObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun initCellsObservable(): Observable<ArrayList<Cell>> {
        return Observable.just(genInitCells()).subscribeOn(Schedulers.io())
    }

    private fun nextChangedCellsObservable(): Observable<ArrayList<Cell>> {
        return Observable.interval(TIME_PERIOD, TimeUnit.SECONDS)
            .flatMap { Observable.fromArray(getNextCells()) }
    }

    private fun genInitCells(): ArrayList<Cell> {
        var c: Cell
        for (i in 0 until colAndRow) {
            for (j in 0 until colAndRow) {
                val isLive = (j == colAndRow / 2 && i in (colAndRow / 2 - 1)..(colAndRow / 2 + 1))
                c = Cell(Point(i, j), isLive)
                if (isLive) {
                    println("init alive ${c.pos}")
                    liveCells[c.pos.toKey()] = c
                } else {
                    deadCells[c.pos.toKey()] = c
                }
            }
        }
        return ArrayList(liveCells.values)
    }

    private fun getNextCells(): ArrayList<Cell> {
        println("getNextCells")
        val needCheckCells = HashSet<Point>()
        for (cell in liveCells.values) {
            needCheckCells.addAll(cell.pos.getNeighborsAndSelf())
        }

        val tasks = mutableListOf<Runnable>()
        for (targetPos in needCheckCells) {
            val c = getCells(targetPos.toKey()) ?: continue

            if (c.live) {
                if (!checkKeepAlive(c)) {
                    tasks.add(Runnable {
                        println("${c.pos}, ${c.live}")
                        c.live = false
                        liveCells.remove(c.pos.toKey())
                        deadCells[c.pos.toKey()] = c
                    })
                }
            } else {
                if (!checkKeepDead(c)) {
                    tasks.add(Runnable {
                        println("${c.pos}, ${c.live}")
                        c.live = true
                        deadCells.remove(c.pos.toKey())
                        liveCells[c.pos.toKey()] = c
                    })
                }
            }
        }

        for (r in tasks) r.run()

        return ArrayList(liveCells.values)
    }

    private fun getCells(key: String): Cell? {
        var cell = liveCells[key]
        if (cell != null) return cell
        cell = deadCells[key]
        return cell
    }

    private fun checkKeepAlive(c: Cell): Boolean {
        var count = 0
        for (neighborPos in c.pos.getNeighborsAndSelf()) {
            val neighborCell = liveCells[neighborPos.toKey()]
            if (neighborCell != null) count++
        }
        //sub it self
        count--
        return count == 2 || count == 3
    }

    private fun checkKeepDead(c: Cell): Boolean {
        var count = 0
        for (neighborPos in c.pos.getNeighborsAndSelf()) {
            val neighborCell = liveCells[neighborPos.toKey()]
            if (neighborCell != null) count++
        }
        return count != 3
    }
    
    private fun Point.getNeighborsAndSelf(): MutableList<Point> {
        val neighbors = mutableListOf<Point> ()
        for (i in (x - 1)..(x + 1)) {
            for (j in (y - 1)..(y + 1)) {
                if (x >= 0 && y >= 0) {
                    neighbors.add(Point(i, j))
                }
            }
        }
        return neighbors
    }
    
    private fun Point.toKey(): String = "$x-$y"
}
