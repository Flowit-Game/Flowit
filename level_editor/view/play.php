<div class="container p-3">
    <?php if (@$solved) { ?>
        <div class="alert alert-success">
            Level was solved. Congratulations.
        </div>
    <?php } ?>
    <div class="row">
        <div class="col card p-0 m-2">
            <div class="card-header">
                Game board
            </div>
            <div class="card-body">
                <?php 
                    drawBoard($_SESSION["play_data"], "play", 0, 0, false, true);
                ?>
            </div>
        </div>
    </div>
</div>