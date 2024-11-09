public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Убраться дома", "Необходимо сделать уборку в квартире");
        Task task2 = new Task("Переезд", "Перевезти вещи в новую квартиру");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Организация свадьбы", "Подготовка к свадьбе");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Купить торт", "Выбрать и заказать свадебный торт", epic1.getId());
        Subtask subtask2 = new Subtask("Пригласить гостей", "Составить список гостей и отправить приглашения", epic1.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Праздник на природе", "Организация пикника");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Купить еду", "Закупить продукты для пикника", epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("Все задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName() + " | Статус: " + task.getStatus());
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getName() + " | Статус: " + epic.getStatus());
            for (Subtask subtask : taskManager.getSubtasksForEpic(epic.getId())) {
                System.out.println("  Подзадача: " + subtask.getName() + " | Статус: " + subtask.getStatus());
            }
        }

        taskManager.updateTaskStatus(task1.getId(), TaskStatus.IN_PROGRESS);
        taskManager.updateTaskStatus(subtask1.getId(), TaskStatus.DONE);
        taskManager.updateTaskStatus(subtask2.getId(), TaskStatus.DONE);

        System.out.println("\nПосле изменения статусов:");
        System.out.println("Все задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName() + " | Статус: " + task.getStatus());
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getName() + " | Статус: " + epic.getStatus());
            for (Subtask subtask : taskManager.getSubtasksForEpic(epic.getId())) {
                System.out.println("  Подзадача: " + subtask.getName() + " | Статус: " + subtask.getStatus());
            }
        }

        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic2.getId());

        System.out.println("\nПосле удаления задачи и эпика:");
        System.out.println("Все задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getName() + " | Статус: " + task.getStatus());
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getName() + " | Статус: " + epic.getStatus());
            for (Subtask subtask : taskManager.getSubtasksForEpic(epic.getId())) {
                System.out.println("  Подзадача: " + subtask.getName() + " | Статус: " + subtask.getStatus());
            }
        }
    }
}



