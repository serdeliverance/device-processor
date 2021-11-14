package deviceprocessor.queue

trait DQueue[T] {
  def enqueue(t: T): DQueue[T]
  def dequeue: (T, DQueue[T])
  def getAll: List[T]
}
