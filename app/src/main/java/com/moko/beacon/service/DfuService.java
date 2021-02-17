/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.moko.beacon.service;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        /*
		 * As a target activity the NotificationActivity is returned, not the MainActivity. This is because the notification must create a new task:
		 *
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 *
		 * when user press it. Using NotificationActivity we can check whether the new activity is a root activity (that means no other activity was open before)
		 * or that there is other activity already open. In the later case the notificationActivity will just be closed. System will restore the previous activity from
		 * this application - the MainActivity. However if nRF Beacon has been closed during upload and user click the notification a NotificationActivity will
		 * be launched as a root activity. It will create and start the MainActivity and finish itself.
		 *
		 * This method may be used to restore the target activity in case the application was closed or is open. It may also be used to recreate an activity history (see NotificationActivity).
		 */

        return null;
    }

}
/*
 * En tant qu'activité cible, la NotificationActivity est renvoyée, pas la MainActivity. En effet, la notification doit créer une nouvelle tâche:
*
* intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
*
* lorsque l'utilisateur appuie dessus. En utilisant NotificationActivity, nous pouvons vérifier si la nouvelle activité est une activité racine (cela signifie qu'aucune autre activité n'était ouverte auparavant)
* ou qu'il existe une autre activité déjà ouverte. Dans le dernier cas, la notificationActivity sera simplement fermée. Le système restaurera l'activité précédente à partir de
* cette application - la MainActivity. Cependant, si la balise nRF a été fermée pendant le téléchargement et que l'utilisateur clique sur la notification, une activité de notification sera
* être lancé en tant qu'activité racine. Il va créer et démarrer la MainActivity et se terminer.
*
* Cette méthode peut être utilisée pour restaurer l'activité cible au cas où l'application était fermée ou ouverte. Il peut également être utilisé pour recréer un historique d'activité (voir NotificationActivity).
 */