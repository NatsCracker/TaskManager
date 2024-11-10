public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создаем две обычные задачи
        Task task1 = new Task("Убраться дома", "Навести порядок в доме");
        Task task2 = new Task("Купить продукты", "Закупить необходимые продукты");
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = new Epic("Организовать праздник", "Организация семейного праздника");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Купить торт", "Закупить праздничный торт", epic1.getId());
        Subtask subtask2 = new Subtask("Пригласить гостей", "Отправить приглашения гостям", epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // Создаем эпик с одной подзадачей
        Epic epic2 = new Epic("Переезд", "Переезд в новую квартиру");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Упаковать вещи", "Упаковать все вещи для переезда", epic2.getId());
        manager.createSubtask(subtask3);

        // Печать всех задач, эпиков и подзадач
        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе подзадачи эпика 'Организовать праздник':");
        for (Subtask subtask : manager.getSubtasksForEpic(epic1.getId())) {
            System.out.println(subtask);
        }

        System.out.println("\nВсе подзадачи эпика 'Переезд':");
        for (Subtask subtask : manager.getSubtasksForEpic(epic2.getId())) {
            System.out.println(subtask);
        }

        // Изменение статусов
        manager.updateTaskStatus(task1.getId(), TaskStatus.IN_PROGRESS);
        manager.updateSubtaskStatus(subtask1.getId(), TaskStatus.DONE);
        manager.updateSubtaskStatus(subtask2.getId(), TaskStatus.DONE);
        manager.updateSubtaskStatus(subtask3.getId(), TaskStatus.IN_PROGRESS);

        // Печать статусов после обновлений
        System.out.println("\nСтатусы задач после обновлений:");
        System.out.println(manager.getTaskById(task1.getId()));
        System.out.println(manager.getTaskById(task2.getId()));

        System.out.println("\nСтатус эпика 'Организовать праздник' после обновлений подзадач:");
        System.out.println(manager.getEpicById(epic1.getId()));

        System.out.println("\nСтатус эпика 'Переезд' после обновления подзадачи:");
        System.out.println(manager.getEpicById(epic2.getId()));

        // Удаление задачи и эпика
        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic1.getId());

        // Печать задач и эпиков после удаления
        System.out.println("\nВсе задачи после удаления одной из них:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе эпики после удаления одного из них:");
        System.out.println(manager.getEpicById(epic1.getId()));  // должен быть null после удаления
        System.out.println(manager.getEpicById(epic2.getId()));  // должен существовать
    }
}
