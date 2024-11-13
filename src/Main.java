public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создаем обычные задачи
        Task task1 = new Task("Уборка", "Убраться в квартире");
        Task task2 = new Task("Покупки", "Сходить в магазин за продуктами");
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаем первый эпик с двумя подзадачами
        Epic epic1 = new Epic("Организация вечеринки", "Подготовка к вечеринке на выходных");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Купить торт", "Заказать торт в кондитерской", epic1.getId());
        Subtask subtask2 = new Subtask("Пригласить друзей", "Создать чат и пригласить друзей", epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // Создаем второй эпик с одной подзадачей
        Epic epic2 = new Epic("Переезд", "Подготовка к переезду в новую квартиру");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Упаковка вещей", "Упаковать все в коробки", epic2.getId());
        manager.createSubtask(subtask3);

        // Печать всех задач
        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(" - " + task.getId() + ": " + task.getName() + ": " + task.getDescription() + " [Статус: " + manager.getTaskStatusById(task.getId()) + "]");
        }

        // Печать всех эпиков
        System.out.println("\nВсе эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(" - " + epic.getId() + ": " + epic.getName() + ": " + epic.getDescription() + " [Статус: " + manager.getTaskStatusById(epic.getId()) + "]");
        }

        //Печать всех подзадач
        System.out.println("\nВсе подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(" - " + subtask.getId() + ": " + subtask.getName() + ": " + subtask.getDescription() + " [Статус: " + manager.getTaskStatusById(subtask.getId()) + "]");
        }

        // Печать подзадач для каждого эпика
        System.out.println("\nПодзадачи для эпика 'Организация вечеринки':");
        for (Subtask subtask : manager.getSubtasksForEpic(epic1.getId())) {
            System.out.println(" - " + subtask.getName() + ": " + subtask.getDescription() + " [Статус: " + manager.getTaskStatusById(subtask.getId()) + "]");
        }

        System.out.println("\nПодзадачи для эпика 'Переезд':");
        for (Subtask subtask : manager.getSubtasksForEpic(epic2.getId())) {
            System.out.println(" - " + subtask.getName() + ": " + subtask.getDescription() + " [Статус: " + manager.getTaskStatusById(subtask.getId()) + "]");
        }

        // Изменение статусов задач и подзадач
        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);

        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        // Проверка статусов после изменений
        System.out.println("\nСтатусы после изменений:");
        System.out.println("Задача 'Уборка' - [Статус: " + manager.getTaskStatusById(task1.getId()) + "]");
        System.out.println("Подзадача 'Купить торт' - [Статус: " + manager.getTaskStatusById(subtask1.getId()) + "]");
        System.out.println("Эпик 'Организация вечеринки' - [Статус: " + manager.getTaskStatusById(epic1.getId()) + "]");

        // Удаление подзадачи по ID и пересчет статуса эпика
        manager.deleteSubtaskById(subtask2.getId());
        System.out.println("\nПосле удаления подзадачи 'Пригласить друзей':");
        System.out.println("Эпик 'Организация вечеринки' - [Статус: " + manager.getTaskStatusById(epic1.getId()) + "]");

        // Удаление задачи и эпика
        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());

        // Печать задач после удаления
        System.out.println("\nСписок задач после удаления:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(" - " + task.getName() + ": " + task.getDescription() + " [Статус: " + manager.getTaskStatusById(task.getId()) + "]");
        }

        // Удаление всех задач и подзадач
        System.out.println("\nПосле удаления всех задач и подзадач:");
        manager.deleteAllTasks();
        if (manager.getAllTasks().isEmpty()) {
            System.out.println("Нет задач.");
        }
        manager.deleteAllSubtasks();
        if (manager.getAllSubtasks().isEmpty()) {
        System.out.println("Нет подзадач.");
        }
        manager.deleteAllEpics();
        if (manager.getAllEpics().isEmpty()) {
        System.out.println("Нет эпиков.");
        }
    }
}
