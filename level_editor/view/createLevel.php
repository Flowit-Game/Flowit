<div class="container p-3">
    <?php
        if (@$sendResult != "") {
            echo $sendResult;
        }
    ?>
    <div class="alert alert-primary screen-size-warning">
        This website works best on big PC screens.
    </div>
    <h4 class="m-1">Welcome to the Flowit Level Editor</h4>
    <p>
        This website allows to design a level and submit the level to be included in future updates of Flowit.
    </p>
    <a href="?cols=5&rows=6&r=0&c=0&action=edit" class="btn btn-secondary m-1">Create small level</a>
    <br/>
    <a href="?cols=6&rows=8&r=0&c=0&action=edit" class="btn btn-secondary m-1">Create big level</a>
</div>